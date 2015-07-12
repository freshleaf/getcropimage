package com.dengyuman.getcropimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Xingye on 6/18/2015.
 */
public class StorageUtils {

    private static boolean isExternalCreated = false;
    private static boolean isInternalCreated = false;

    private static final String INTERNAL_DIR = ".getcropimage";
    private static final String EXTERNAL_DIR = ".getcropimage";

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private static String getCacheFileFolder(Context context) {
        String path = null;
        File dir = null;

        if (isExternalStorageWritable()) {
            try {
                dir = context.getExternalCacheDir();
                path = dir.getAbsolutePath();
            } catch (Exception e) {
                // rare devices will throw NullPointerException
                dir = context.getCacheDir();
                path = dir.getAbsolutePath();
            }
        } else {
            dir = context.getCacheDir();
            path = dir.getAbsolutePath();
        }

        return path;
    }

    public static String getRootFileFolder(Context context) {
        String path = null;
        File dir = null;

        if (isExternalStorageWritable()) {
            dir = context.getExternalFilesDir(null);
            path = dir.getAbsolutePath() + File.separator + EXTERNAL_DIR;
            if (!isExternalCreated) {
                File f = new File(path);
                f.mkdirs();
                isExternalCreated = true;
            }
        } else {
            dir = context.getDir(INTERNAL_DIR, Context.MODE_PRIVATE);
            path = dir.getAbsolutePath();
            if (!isInternalCreated) {
                File f = new File(path);
                f.mkdirs();
                isInternalCreated = true;
            }
        }

        return path;
    }

    /**
     * create a URI point to a new image file
     *
     * @param context
     * @return
     */
    public static Uri getNewCacheImageFileUri(Context context) {
        String tmpFileName = "tmp_camera_" + String.valueOf(System.currentTimeMillis()) + ".jpg";

        Uri uri = Uri.fromFile(new File(getCacheFileFolder(context), tmpFileName));
        return uri;
    }

    public static String getStoredImageFilePath(Context context) {
        String fileName = "stored_"
                + String.valueOf(System.currentTimeMillis()) + ".jpg";
        return getRootFileFolder(context) + File.separator + fileName;
    }

    // test result: quality 85: 82.4kb, q 80: 53.5kb, q 100: 338kb
    public static String saveCompressedImage(Context context, Bitmap bitmap, int quality)
    throws IOException {
        String path = getStoredImageFilePath(context);
        return saveCompressedImage(context, bitmap, quality, path);
    }

    public static String saveCompressedImage(Context context, Bitmap bitmap, int quality, String filePath)
            throws IOException {
        if (bitmap == null) {
            return null;
        }
        String path = filePath;
        FileOutputStream out = null;
        out = new FileOutputStream(filePath);
        try {
            // test result: quality 85: 82.4kb, q 80: 53.5kb, q 100: 338kb
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        } catch (Exception e) {
            path = null;
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return path;
    }

}
