package com.tgcyber.hotelmobile.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;

import com.tgcyber.hotelmobile._utils.LogCat;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ImagetUtils {

    public static Bitmap generateCircleImage(Bitmap bitmap) {
        return toRoundBitmap(bitmap);
    }

    public static Bitmap getBitmapFromByte(byte[] temp) {
        if (temp != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        } else {
            return null;
        }
    }

    public static Bitmap createScaledBitmap(Bitmap bitmap, int w, int h) {
        /*
         * int width = w + 1; int height = h + 1; int t_width; int t_height;
		 */

        float bh = bitmap.getHeight();
        float bw = bitmap.getWidth();
        float pri = 1.0f;
        float wpri = bw / w;
        float hpri = bh / h;

        if (wpri > hpri)
            pri = hpri;
        else
            pri = wpri;
        LogCat.i("ImagetUtils", "bitmap.w=" + bw + " bitmap.h=" + bh + " pri="
                + pri);
        LogCat.i("ImagetUtils", "2bitmap.w=" + bw / pri + " bitmap.h=" + bh
                / pri + " pri=" + pri);
        return Bitmap.createScaledBitmap(bitmap, (int) (bw / pri),
                (int) (bh / pri), true);

    }


    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;

            left = 0;
            top = 0;
            right = width;
            bottom = width;

            height = width;

            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;

            float clip = (width - height) / 2;

            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;

            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = null;
        try {
            output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final Paint paint = new Paint();
            final Rect src = new Rect((int) left, (int) top, (int) right,
                    (int) bottom);
            final Rect dst = new Rect((int) dst_left, (int) dst_top,
                    (int) dst_right, (int) dst_bottom);
            final RectF rectF = new RectF(dst);

            paint.setAntiAlias(true);// ���û����޾��

            canvas.drawARGB(0, 0, 0, 0); // ������Canvas

            // ���������ַ�����Բ,drawRounRect��drawCircle
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);// ��Բ�Ǿ��Σ���һ������Ϊͼ����ʾ���򣬵ڶ�������͵��������ֱ���ˮƽԲ�ǰ뾶�ʹ�ֱԲ�ǰ뾶��
            // canvas.drawCircle(roundPx, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));// ��������ͼƬ�ཻʱ��ģʽ,�ο�http://trylovecatch.iteye.com/blog/1189452
            canvas.drawBitmap(bitmap, src, dst, paint); // ��Mode.SRC_INģʽ�ϲ�bitmap���Ѿ�draw�˵�Circle
        } catch (Exception e) {

        } catch (OutOfMemoryError e) {

        }

        return output;
    }

    public static String bitmapCompress(String destDir, String filePath) {
        File src = new File(filePath);
        if (!src.isFile() || src.length() <= 0) {
            return null;
        }

        File dest = new File(destDir, System.currentTimeMillis() + "_small_"
                + src.getName());
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            Bitmap bm = getSmallBitmap(filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);

            if (!bm.isRecycled()) {
                bm.recycle();
            }
            File parent = dest.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            fos = new FileOutputStream(dest);
            bos = new BufferedOutputStream(fos);
            byte[] b = baos.toByteArray();
            fos.write(b);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            // TODO: handle exception
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        if (dest == null || dest.length() <= 0) {
            return null;
        }
        return dest.getAbsolutePath();

    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        Matrix matrix = new Matrix();
        int angle = readPictureDegree(filePath);
        LogCat.i("ImagetUtils", "angle = " + angle);
        matrix.postRotate(angle);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 800, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap b = null;
        Bitmap finalB = null;
        try {
            b = BitmapFactory.decodeFile(filePath, options);
            if (angle == 0) {
                finalB = b;
            } else {
                finalB = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
                        b.getHeight(), matrix, true);
                b.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
    e.printStackTrace();
        }
        return finalB;
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degree;
    }
}
