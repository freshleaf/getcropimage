package com.dengyuman.getcropimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by Xingye on 6/18/2015.
 */
public class ImageUtils {

    /**
     * scale down image and keep ratio, width or height satisfy: <br>
     * 1/2 max width < resized image width < max width <br>
     * or <br>
     * 1/2 max height < resized image height < max height <br>
     *
     * @param context
     * @param uri             image file uri
     * @param isStrictSmaller true: make resized bitmap smaller than target
     *                        false: make resized bitmap just larger than target and smaller than twice size of target
     * @param targetWidth     target bitmap width limitation
     * @param targetHeight    target bitmap height limitation
     * @return
     * @throws IOException
     */
    public static Bitmap getResizedBitmap(Context context, Uri uri, boolean isStrictSmaller,
                                          int targetWidth, int targetHeight) throws IOException {

        ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver()
                .openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, op);
        int wRatio = (op.outWidth + targetWidth - 1) / targetWidth;
        int hRatio = (op.outHeight + targetHeight - 1) / targetHeight;
        if (wRatio > 1 || hRatio > 1) {
            // need resize
            if (isStrictSmaller) {
                int ratio = wRatio;
                if (wRatio < hRatio) {
                    ratio = hRatio;
                }
                int level = 1;
                while (ratio > level) {
                    level *= 2;
                }
                ratio = level;
                op.inSampleSize = ratio;
            } else {
                if (wRatio < hRatio) {
                    op.inSampleSize = wRatio;
                } else {
                    op.inSampleSize = hRatio;
                }
            }
        }
        op.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, op);
        parcelFileDescriptor.close();

        try {
            ExifInterface exif = new ExifInterface(uri.getPath());
            bmp = rotateBitmap(bmp, exif);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    /**
     * scale down image and keep ratio, width or height satisfy: <br>
     * 1/2 max width < resized image width < max width <br>
     * or <br>
     * 1/2 max height < resized image height < max height <br>
     *
     * @param path            image file path
     * @param isStrictSmaller true: make resized bitmap smaller than target
     *                        false: make resized bitmap just larger than target and smaller than twice size of target
     * @param targetWidth     target bitmap width limitation
     * @param targetHeight    target bitmap height limitation
     * @return
     * @throws IOException
     */
    public static Bitmap getResizedBitmap(String path, boolean isStrictSmaller, int targetWidth,
                                          int targetHeight) {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path, op);
        int wRatio = (op.outWidth + targetWidth - 1) / targetWidth;
        int hRatio = (op.outHeight + targetHeight - 1) / targetHeight;
        if (wRatio > 1 || hRatio > 1) {
            // need resize
            if (isStrictSmaller) {
                int ratio = wRatio;
                if (wRatio < hRatio) {
                    ratio = hRatio;
                }
                int level = 1;
                while (ratio > level) {
                    level *= 2;
                }
                ratio = level;
                op.inSampleSize = ratio;
            } else {
                if (wRatio < hRatio) {
                    op.inSampleSize = wRatio;
                } else {
                    op.inSampleSize = hRatio;
                }
            }
        }
        op.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(path, op);

        try {
            ExifInterface exif = new ExifInterface(path);
            bmp = rotateBitmap(bmp, exif);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri)
            throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = context
                .getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor
                .getFileDescriptor();
        Bitmap bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();

        try {
            ExifInterface exif = new ExifInterface(uri.getPath());
            bmp = rotateBitmap(bmp, exif);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    private static Bitmap rotateBitmap(Bitmap bitmap, ExifInterface exif) {
        if (bitmap == null || exif == null) {
            return bitmap;
        }
        int rotate = 0;
        int result = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        switch (result) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            default:
                break;
        }
        if (rotate != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            Bitmap rotateBitmap = Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (rotateBitmap != null) {
                bitmap.recycle();
                bitmap = rotateBitmap;
            }
        }
        return bitmap;
    }
}
