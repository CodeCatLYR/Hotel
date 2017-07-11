package com.tgcyber.hotelmobile.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.DateFormat;

import com.tencent.smtt.sdk.CacheManager;
import com.tgcyber.hotelmobile.constants.Constants;
import com.tgcyber.hotelmobile._utils.LogCat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.text.DecimalFormat;

import static android.os.Environment.MEDIA_MOUNTED;

public class IOUtil {

    public static boolean checkSDCardAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }
    public static long getFileSize(File path) {
        // List<File> list = new ArrayList<File>();
        long size = 0;
        for (File item : path.listFiles()) {
            if (item.isDirectory()) {
                if (item.equals(new File(Constants.DOWNLOAD_PATH))) {
                    LogCat.i("IOUtil", Constants.DOWNLOAD_PATH + " keep!");
                    continue;
                }
                size = size + getFileSize(item);// , now);
            } else {
                size = size + item.length();
            }
        }
        return size;
        // if(path.listFiles().length==0)path.delete();
    }

    public static String formetFileSize(long fileS) {// ת���ļ���С
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
            if (fileSizeString.indexOf(".") == 0) {
                fileSizeString = "0" + fileSizeString;
            }
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * app地址
     *
     * @return
     */
    public static boolean createAppPath() {
        try {
            File file = new File(Constants.APP_PATH);
            if (!file.exists()) {
                return file.mkdirs();
            }
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 图片下载地址
     *
     * @return
     */
    public static boolean createDownloadPath() {
        try {
            File file = new File(Constants.DOWNLOAD_PATH);
            if (!file.exists()) {
                return file.mkdirs();
            }
            return true;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }

    /**
     * 照相
     */
    public static boolean createCameraPath() {
        try {
            File file = new File(Constants.CAMERA_PATH);
            if (!file.exists()) {
                return file.mkdirs();
            }
            return true;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }



    /**
     * 缓存
     */
    public static boolean createDataPath() {
        try {
            File file = new File(Constants.DATA_PATH);
            if (!file.exists()) {
                return file.mkdirs();
            }
            return true;
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;
    }

    public static Uri addImageAsCamera(ContentResolver cr, Bitmap bitmap) {

        long dateTaken = System.currentTimeMillis();

        String name = createName(dateTaken) + ".jpg";

        String uriStr = MediaStore.Images.Media.insertImage(cr, bitmap, name,
                null);

        return Uri.parse(uriStr);

    }

    private static String createName(long dateTaken) {

        return DateFormat.format("yyyy-MM-dd_kk.mm.ss", dateTaken).toString();

    }

    public static boolean fileISExists(String path) {
        if (null == path)
            return false;

        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static void saveUserPhoto(Bitmap bm, String fileName) {
        if (null == bm || null == fileName)
            return;
        if (createCameraPath()) {
            File myCaptureFile = new File(Constants.CAMERA_PATH + fileName);
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(
                        myCaptureFile));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (null != bos) {
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                try {
                    bos.flush();
                    bos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }


    public static void saveImg2Download(byte[] data, String filePath) {
        if (!createDownloadPath() || null == filePath)
            return;

        File imageFile = new File(filePath);
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(imageFile);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (null != outStream) {
            try {
                outStream.write(data);
                outStream.close();
                LogCat.i("IOUtil","saveImg2Download:"+filePath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public static void initAppDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context.getFilesDir();
        }
        Constants.APP_PATH=appCacheDir.getAbsolutePath()+  File.separator;

        //  LogCat.i("IOUtil","download.mkdirs="+a);
        createCameraPath();
        createDownloadPath();
        //return appCacheDir;
    }
    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    /**
     * �ӱ���Ŀ¼�ж�ȡͼƬ
     *
     * @param filePath
     * @param fileName
     * @return
     */
    public static Bitmap readImgFromLocal(String filePath, String fileName) {
        if (null != filePath && null != fileName && fileName.length() > 0) {
            Bitmap bm = null;
            bm = BitmapFactory.decodeFile(filePath + fileName);
            return bm;
        }
        return null;
    }

    public static boolean checkSdCard() {
        boolean state = false;
        String sDStateString = android.os.Environment.getExternalStorageState();
        if (sDStateString.equals(android.os.Environment.MEDIA_MOUNTED)) {
            state = true;
        }
        sDStateString = null;
        return state;
    }

    public static String readFile(String path, String fileName) {
        String res = null;
        if (checkSdCard()) {
            if (path != null && fileName != null)
                fileName = path + fileName;
            File f = new File(fileName);
            if (!f.exists())
                return res;

            FileInputStream fin = null;
            try {
                fin = new FileInputStream(f);

                byte[] buf = new byte[fin.available()];
                fin.read(buf);

                res = new String(buf, "UTF-8");

                buf = null;
            } catch (FileNotFoundException e) {
                // e.printStackTrace();
                LogCat.i("IOUtil", "File readFile FileNotFoundException:"
                        + fileName);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fin != null)
                        fin.close();
                } catch (IOException e) {

                }
            }
        }

        return res;
    }

    public static boolean saveBmFile(Bitmap bm, String fileName) {

        boolean state = false;
        if (checkSdCard() && createDataPath()) {

            String t[] = fileName.split("/");
            if (t.length > 1) {
                fileName = t[t.length - 1];
            } else {
                t = null;
                return false;
            }
            if (fileISExists(Constants.DATA_PATH + fileName))
                return false;
            t = null;
            File file = new File(Constants.DATA_PATH + fileName);
            FileOutputStream os = null;
            BufferedOutputStream bos = null;
            try {
                if (!file.exists())
                    file.createNewFile();
                os = new FileOutputStream(file);
                bos = new BufferedOutputStream(os);// new
                bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                bos.flush();
                state = true;
                LogCat.i("IOUtil", "BM Saved Pass:" + Constants.DATA_PATH
                        + fileName);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null)
                        bos.close();
                    if (os != null)
                        os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            file = null;
        }
        return state;

    }

    public static Bitmap loadLocal(String fileName) {
        Bitmap bitmap = null;
        if (checkSdCard()) {

            String t[] = fileName.split("/");
            if (t.length > 1) {
                fileName = t[t.length - 1];
            } else
                return null;
            t = null;
            LogCat.i("IOUtil", "loadBitmap()A:" + Constants.DATA_PATH
                    + fileName);
            File file = new File(Constants.DATA_PATH + fileName);
            if (!file.exists())
                return null;
            long size = file.length();
            if (size < 1)
                return null;
            FileInputStream is = null;
            int sampleSize = 0;
            try {
                is = new FileInputStream(file);// .openFileInput(fileName);
                bitmap = BitmapFactory.decodeStream(is);
                LogCat.i("IOUtil", "loadBitmap() Pass:" + Constants.DATA_PATH
                        + fileName + "  size=" + (file.length() / 1024)
                        + " type=" + sampleSize);
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
                bitmap = null;
            } finally {
                if (is != null)
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
            }
        }
        fileName = null;
        return bitmap;
    }

    public static Bitmap loadBitmap(String fileName, boolean isReadNet) {
        Bitmap bitmap = null;
        if (isReadNet) {
            bitmap = AsyncImageLoader.loadImageDrawableFromUrl_o(fileName);
            if (bitmap == null)
                bitmap = loadLocal(fileName);
            else
                saveBmFile(bitmap, fileName);
        } else {
            bitmap = loadLocal(fileName);
            if (bitmap == null) {
                bitmap = AsyncImageLoader.loadImageDrawableFromUrl_o(fileName);
                if (bitmap != null)
                    saveBmFile(bitmap, fileName);
            }
        }
        return bitmap;
    }

	/*
     * public static void saveFile(String content, String fileName ) { if
	 * (!createAdPath() || null == fileName) return;
	 * 
	 * File imageFile = new File(fileName); FileOutputStream outStream = null;
	 * try { outStream = new FileOutputStream(imageFile); } catch
	 * (FileNotFoundException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } if (null != outStream) { try {
	 * outStream.write(fileName.getBytes()); outStream.close(); } catch
	 * (IOException e) { e.printStackTrace(); } }
	 * 
	 * 
	 * }
	 */

    /**
     * ������󵽻���Ŀ¼
     *
     * @param object
     * @param fileName
     */
    public static void saveObject2Cache(Object object, String fileName) {
        if (null != object && null != fileName && fileName.length() > 0) {
            if (createDataPath()) {
                ObjectOutputStream oos = null;
                String path = Constants.DATA_PATH + fileName;
                try {
                    oos = new ObjectOutputStream(new FileOutputStream(new File(
                            path)));
                    oos.writeObject(object);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (oos != null) {
                        try {
                            oos.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * ����ļ���ӻ���Ŀ¼�ж�ȡ����
     *
     * @param fileName
     * @return
     */
    public static Object readObjectFromCache(String fileName) {
        if (null != fileName && fileName.length() > 0) {
            String path = Constants.DATA_PATH + fileName;
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(new File(path)));
                Object obj = ois.readObject();
                return obj;
            } catch (StreamCorruptedException e) {
                e.printStackTrace();
                return null;
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                return null;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (ois != null) {
                        ois.close();
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static InputStream Bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

    /**
     * ɾ����ļ�
     *
     * @param sPath ��ɾ���ļ����ļ���
     * @return �����ļ�ɾ��ɹ�����true�����򷵻�false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        try {
            File file = new File(sPath);
            if (file.isFile() && file.exists()) {
                flag = file.delete();
            } else {
                return flag;
            }
        } catch (Exception e) {

        }
        return flag;
    }

    /**
     * ɾ��Ŀ¼�µ��ļ�(�������ļ����µ��ļ�)
     *
     * @param sPath ��ɾ��Ŀ¼���ļ�·��
     * @return Ŀ¼ɾ��ɹ�����true�����򷵻�false
     */
    public static boolean deleteDirectory(String sPath) {
        // ���sPath�����ļ��ָ����β���Զ�����ļ��ָ���
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        // ���dir��Ӧ���ļ������ڣ����߲���һ��Ŀ¼�����˳�
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // ɾ���ļ����µ������ļ�(������Ŀ¼)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // ɾ�����ļ�
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } else { // ɾ����Ŀ¼
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        return flag;
    }

    public static boolean deleteCache() {
        // ���sPath�����ļ��ָ����β���Զ�����ļ��ָ���
        String sPath = Constants.CACHE_PATH;
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        // ���dir��Ӧ���ļ������ڣ����߲���һ��Ŀ¼�����˳�
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        // ɾ���ļ����µ������ļ�(������Ŀ¼)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // ɾ�����ļ�
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        return flag;
    }

    public static void delFile(File path) {
        // List<File> list = new ArrayList<File>();
        for (File item : path.listFiles()) {
            if (item.isDirectory()) {
                delFile(item);// , now);
            } else {
                try {
                        item.delete();
                } catch (Exception e) {
                    LogCat.i("IOUtil", item.getName());
                    e.printStackTrace();
                }

            }
        }
        // if(path.listFiles().length==0)path.delete();
    }

    public static long getHistoryFileSize() {
        long size = 0;
        File file = new File(Constants.APP_PATH);
        try {
            if (file.exists()) {

                size = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // delFile(file, wait, now);
        LogCat.i("IOUtil", Constants.APP_PATH + " COUNT=" + size);
        return size;
    }

    public static boolean cleanHistoryFile() {

        File file = new File(Constants.APP_PATH);
        try {
            if (file.exists())
                delFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
        // delFile(file, wait, now);
    }
    public static boolean cleanHistoryFile(String path) {

        File file = new File(path);
        try {
            if (file.exists())
                delFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
        // delFile(file, wait, now);
    }
    public static boolean cleanWebViewCacheFile() {

        File file = CacheManager.getCacheFileBaseDir();
        try {
            if (file.exists())
                delCacheFile(file);
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
        return true;
        // delFile(file, wait, now);
    }
    public static void delCacheFile(File path) {
        for (File item : path.listFiles()) {
            if (item.isDirectory()) {
                delCacheFile(item);
            } else {
                try {
                        item.delete();
                } catch (Exception e) {
                    LogCat.i("IOUtil", item.getName());
                }

            }
        }
        // if(path.listFiles().length==0)path.delete();
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

}
