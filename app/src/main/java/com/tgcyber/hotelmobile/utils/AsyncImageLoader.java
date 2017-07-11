package com.tgcyber.hotelmobile.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.tgcyber.hotelmobile._utils.LogCat;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;

public class AsyncImageLoader {

    public static HashMap<String, SoftReference<Bitmap>> bitmapCache = new HashMap<String, SoftReference<Bitmap>>();

    public AsyncImageLoader() {
    }

    private final static String TAG = "AsyncImageLoader";

    public static Bitmap loadBitmap(final String imageUrl,
                                    final BitmapCallback imageCallback, final boolean refresh,
                                    String catalogId) {
        LogCat.i(TAG, "loadBitmap:" + imageUrl);
        if (imageUrl == null || imageUrl.toLowerCase().indexOf(".jpg") <= 0)
            return null;

		/*
         * if (catalogId !=
		 * null&&(catalogId.equals(Config.YINGXIANG_B)||catalogId
		 * .equals(Config.XW_BIG))) { ArrayList<String> list =
		 * CacheItems.CACHE_HISTORY.get(catalogId); if (list == null) list = new
		 * ArrayList<String>(); list.add(imageUrl);
		 * CacheItems.CACHE_HISTORY.put(catalogId, list); }
		 */
        if (bitmapCache.containsKey(imageUrl)) {
            SoftReference<Bitmap> softReference = bitmapCache.get(imageUrl);
            Bitmap bitmap = softReference.get();
            if (bitmap != null) {
                imageCallback.imageLoaded(bitmap, imageUrl);
                return bitmap;
            }
        }
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.obj != null) {
                    imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Bitmap bm = null;
                try {
                    if (!refresh)
                        bm = IOUtil.loadBitmap(imageUrl, refresh);
                    if (bm != null) {
                        IOUtil.saveBmFile(bm, imageUrl);
                    }

                    if (bm != null) {
                        bitmapCache
                                .put(imageUrl, new SoftReference<Bitmap>(bm));
                        handler.sendMessage(handler.obtainMessage(0, bm));
                    }
                } catch (Exception e) {
                    LogCat.i(TAG, imageUrl);
                    e.printStackTrace();
                }

            }
        }.start();

        return null;
    }

    public static Bitmap loadBitmap(final String imageUrl, final Bitmap bmb,
                                    final BitmapCallback imageCallback, final boolean refresh,
                                    String catalogId) {
        LogCat.i(TAG, "loadBitmap:" + imageUrl);
        if (imageUrl == null || imageUrl.toLowerCase().indexOf(".jpg") <= 0)
            return null;

        if (bitmapCache.containsKey(imageUrl)) {
            SoftReference<Bitmap> softReference = bitmapCache.get(imageUrl);
            Bitmap bitmap = softReference.get();
            if (bitmap != null) {
                imageCallback.imageLoaded(bitmap, imageUrl);
                return bitmap;
            }
        }
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.obj != null) {
                    imageCallback.imageLoaded((Bitmap) message.obj, imageUrl);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Bitmap bm = bmb;
                try {

                    if (bm != null) {
                        bitmapCache
                                .put(imageUrl, new SoftReference<Bitmap>(bm));
                        handler.sendMessage(handler.obtainMessage(0, bm));
                    }
                } catch (Exception e) {
                    LogCat.i(TAG, imageUrl);
                    e.printStackTrace();
                }

            }
        }.start();

        return null;
    }

    public static Bitmap loadImageDrawableFromUrl_o(String imgUrl) {
        Bitmap bm = null;
        URLConnection conn = null;
        InputStream is = null;
        try {
            URL url = new URL(imgUrl);
            LogCat.i(TAG, "loadImageDrawableFromUrl_o:" + imgUrl);
            conn = url.openConnection();
            conn.connect();
            is = conn.getInputStream();
            int length = (int) conn.getContentLength();
            if (length != -1) {

                LogCat.i(TAG, "正常读取图片：" + length / 1024);
                byte[] imgData = new byte[length];
                byte[] temp = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(temp)) > 0) {
                    System.arraycopy(temp, 0, imgData, destPos, readLen);
                    destPos += readLen;
                }
                bm = BitmapFactory.decodeByteArray(imgData, 0, imgData.length);
                imgData = null;
                temp = null;
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (is != null)
                try {
                    is.close();
                } catch (Exception e) {

                }
            conn = null;
        }
        return bm;
    }

	/*
     * public interface ImageCallback{ public void imageLoaded(Drawable
	 * imageDrawable, String imageUrl); }
	 */

    public interface BitmapCallback {
        public void imageLoaded(Bitmap bitmap, String imageUrl);
    }
}
