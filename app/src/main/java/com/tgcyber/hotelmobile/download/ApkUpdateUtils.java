package com.tgcyber.hotelmobile.download;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.tgcyber.hotelmobile.R;
import com.tgcyber.hotelmobile._utils.LogCat;

/**
 * Created by chiclaim on 2016/05/18
 **/
public class ApkUpdateUtils {
    public static final String TAG = ApkUpdateUtils.class.getSimpleName();

   // private static final String KEY_DOWNLOAD_ID = "downloadId";

    public static void download(final Context context,final String url,final String title,final String path) {
        LogCat.i("DownloadManager","download");
        new AlertDialog.Builder(new ContextThemeWrapper(context,
                R.style.AlertDialogCustom))
                .setMessage("是否安装？")
                .setPositiveButton(R.string.str_bt_confirm,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogCat.i("FileDownloadManager", "是否安装B");

                        long downloadId = SpUtils.getInstance(context).getLong(path, -1L);
                        if (downloadId != -1L) {
                            FileDownloadManager fdm = FileDownloadManager.getInstance(context);
                            int status = fdm.getDownloadStatus(downloadId);
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                LogCat.i("FileDownloadManager", "是否安装STATUS_SUCCESSFUL");
                                //启动更新界面
                                Uri uri = fdm.getDownloadUri(downloadId);
                                if (uri != null) {
                                    LogCat.i("FileDownloadManager", "是否安装STATUS_SUCCESSFUL"+uri.getPath());
                                   startInstall(context, uri);
                                  return;
                                  /*  if (compare(getApkInfo(context, uri.getPath()), context))
                                    {
                                        LogCat.i("FileDownloadManager", "startInstall");
                                        startInstall(context, uri);
                                        return;
                                    } else {
                                        fdm.getDm().remove(downloadId);
                                    }*/
                                }
                               start(context, url, title,path);
                            } else if (status == DownloadManager.STATUS_FAILED) {
                                LogCat.i("FileDownloadManager","apk is STATUS_FAILED");
                                start(context, url, title,path);
                            } else {
                                Toast.makeText(context, "apk is already downloading", Toast.LENGTH_LONG).show();
                                LogCat.i("FileDownloadManager","apk is already downloading");
                            }
                        } else {
                            start(context, url, title,path);
                        }

                    }
                })
                .setNegativeButton(R.string.str_bt_cancel, null)
                .create()
                .show();

    }

    private static void start( Context context, String downloadUrl, String title,String path) {
        DownloadManager manager = (DownloadManager) context
                .getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(downloadUrl));
        request.setDescription("更新APP");
        request.allowScanningByMediaScanner();// 设置可以被扫描到
        request.setMimeType("application/vnd.android.package-archive");
        request.setVisibleInDownloadsUi(true);// 设置下载可见
        //request.setNotificationVisibility(request.VISIBILITY_VISIBLE);

       request.setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//下载完成后通知栏任然可见
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));// 解析fileName
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);// 设置下载位置，sdcard/Download/fileName
        long id = manager.enqueue(request);// 加入下载并取得下载ID
      //  long id = FileDownloadManager.getInstance(context).startDownload(url,  title, "下载完成后点击打开");
        LogCat.i("DownloadManager","apk start download " + id+" url="+downloadUrl+" path:"+path);
        SpUtils.getInstance(context).putLong(path, id);
        SpUtils.getInstance(context).putLong("download"+id, id);

    }

    public static void startInstall(Context context, Uri uri) {
        LogCat.i("DownloadManager","startInstall" + uri.getPath());
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }


    /**
     * 获取apk程序信息[packageName,versionName...]
     *
     * @param context Context
     * @param path    apk path
     */
    private static PackageInfo getApkInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            //String packageName = info.packageName;
            //String version = info.versionName;
            //LogCat.i("DownloadManager","packageName:" + packageName + ";version:" + version);
            //String appName = pm.getApplicationLabel(appInfo).toString();
            //Drawable icon = pm.getApplicationIcon(appInfo);//得到图标信息
            return info;
        }
        return null;
    }


    /**
     * 下载的apk和当前程序版本比较
     *
     * @param apkInfo apk file's packageInfo
     * @param context Context
     * @return 如果当前应用版本小于apk的版本则返回true
     */
    private static boolean compare(PackageInfo apkInfo, Context context) {
        if (apkInfo == null) {
            return false;
        }
        try {
            String localPackage = context.getPackageName();
            if (apkInfo.packageName.equals(localPackage)) {
                try {
                    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(localPackage, 0);
                    if (apkInfo.versionCode > packageInfo.versionCode) {
                        return true;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }catch(Exception e)
        {
            return true;
        }
    }
}


