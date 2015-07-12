package com.dengyuman.getcropimage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xingye on 6/16/2015.
 */
public class GetImageUtilsActivity extends Activity {

    public static final int REQUSET_CAMERA = 1000;
    public static final int REQUEST_CAMERA_CROP = 1001;
    public static final int REQUEST_PICK = 1002;
    public static final int REQUEST_PICK_CROP = 1003;
    private static final int REQUEST_TO_CROP = 2000;

    public static final String RESULT_KEY_SAVED_FILE_PATH = "result_saved_file_path";
    /**
     * NOTE: there is size limitation of Intent Extra, so the size must be small
     */
    public static final String RESULT_KEY_THUMBNAIL_BITMAP = "result_thumbnail_bitmap";

    public static final String PARRMETER_REQUEST_TYPE = "param_type";
    public static final String PARAMETER_PREVIEW_WIDTH = "param_preview_width";
    public static final String PARAMETER_PREVIEW_HEIGHT = "param_preview_height";
    public static final String PARAMETER_THUMBNAIL_SIZE = "param_thumbnail_size";
    /**
     * NOTE: if crop image size is above limitation, it will be small (like 250) automatically
     */
    public static final String PRRAMETER_CROP_WIDTH = "param_crop_width";
    /**
     * NOTE: if crop image size is above limitation, it will be small (like 250) automatically
     */
    public static final String PRRAMETER_CROP_HEIGHT = "param_crop_height";

    private static final int DEFAULT_THUMBNAIL_SIZE = 250;
    private static final int DEFAULT_CROP_SIZE = 300;
    private static final int DEFAULT_STORE_SIZE_LIMITATION = 800;
    private static int storeSizeLimitation = DEFAULT_STORE_SIZE_LIMITATION;

    private Uri imageFileUri;
    private String cropFilePath;

    private String tmpImageFile; // tmp file should be delete before quit

    private int getImageRequestType;
    private int previewWidth;
    private int previewHeight;
    private int thumbnailSize;
    private int cropWidth;
    private int cropHeight;

    private static Bitmap previewBitmap;

    /**
     * get preview bitmap if set preview image size, it's available for take camera and pick picture
     *
     * @return
     */
    public static Bitmap getPreviewBitmap() {
        if (previewBitmap == null) {
            return null;
        }
        Bitmap tmp = Bitmap.createBitmap(previewBitmap);
        previewBitmap = null;
        return tmp;
    }

    /**
     * set default stored image size
     *
     * @param size
     */
    public static void setDefaultStoreSizeLimitation(int size) {
        storeSizeLimitation = size;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent inIntent = getIntent();

        getImageRequestType = inIntent.getIntExtra(PARRMETER_REQUEST_TYPE, REQUSET_CAMERA);
        thumbnailSize = inIntent.getIntExtra(PARAMETER_THUMBNAIL_SIZE, DEFAULT_THUMBNAIL_SIZE);
        previewWidth = inIntent.getIntExtra(PARAMETER_PREVIEW_WIDTH, -1);
        previewHeight = inIntent.getIntExtra(PARAMETER_PREVIEW_HEIGHT, -1);
        cropWidth = inIntent.getIntExtra(PRRAMETER_CROP_WIDTH, DEFAULT_CROP_SIZE);
        cropHeight = inIntent.getIntExtra(PRRAMETER_CROP_HEIGHT, DEFAULT_CROP_SIZE);

        switch (getImageRequestType) {
            case REQUSET_CAMERA:
                captureCamera();
                break;
            case REQUEST_CAMERA_CROP:
                captureCameraAndCrop();
                break;
            case REQUEST_PICK:
                pickImage();
                break;
            case REQUEST_PICK_CROP:
                pickImageAndCrop();
                break;
        }
    }

    private void captureCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFileUri = StorageUtils.getNewCacheImageFileUri(this);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
        startActivityForResult(intent, REQUSET_CAMERA);
    }

    private void captureCameraAndCrop() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFileUri = StorageUtils.getNewCacheImageFileUri(this);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageFileUri);
        startActivityForResult(intent, REQUEST_CAMERA_CROP);
    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        startActivityForResult(
                Intent.createChooser(intent, getText(R.string.info_pick_image)),
                REQUEST_PICK);
    }

    private void pickImageAndCrop() {
        Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        } else {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        startActivityForResult(
                Intent.createChooser(intent, getText(R.string.info_pick_image)),
                REQUEST_PICK_CROP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        boolean isNeedPreview = previewWidth > 0 && previewHeight > 0;
        switch (requestCode) {
            case REQUSET_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    Intent resultData = new Intent();
                    try {
                        Bitmap toSaveBitmap = ImageUtils.getResizedBitmap(this, imageFileUri,
                                false, storeSizeLimitation, storeSizeLimitation);
                        // quality 80, save file size is around 50 kb
                        String savedFilePath = StorageUtils.saveCompressedImage(this, toSaveBitmap, 80);
                        resultData.putExtra(RESULT_KEY_SAVED_FILE_PATH, savedFilePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // thumbnail
                    try {
                        Bitmap thumbnail = ImageUtils.getResizedBitmap(this, imageFileUri,
                                true, thumbnailSize, thumbnailSize);
                        resultData.putExtra(RESULT_KEY_THUMBNAIL_BITMAP, thumbnail);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // preview
                    if (isNeedPreview) {
                        try {
                            Bitmap preview = ImageUtils.getResizedBitmap(this, imageFileUri,
                                    false, previewWidth, previewHeight);
                            previewBitmap = preview;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    setResult(Activity.RESULT_OK, resultData);
                } else {
                    setResult(resultCode);
                }
                tmpImageFile = imageFileUri.getPath();
                if (tmpImageFile != null) {
                    new File(tmpImageFile).delete();
                }
                finish();
                break;
            case REQUEST_CAMERA_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    tmpImageFile = imageFileUri.getPath();
                    doCrop();
                } else {
                    setResult(resultCode);
                    finish();
                }
                break;
            case REQUEST_PICK:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImageUri = data.getData();

                    Bitmap pickedImage = null;
                    String pickedImageFilePath = null;
                    Intent resultData = new Intent();
                    try {
                        pickedImage = ImageUtils.getResizedBitmap(this, selectedImageUri, false,
                                storeSizeLimitation, storeSizeLimitation);
                        pickedImageFilePath = StorageUtils.saveCompressedImage(this, pickedImage, 80);
                        pickedImage.recycle();
                        pickedImage = null;
                        resultData.putExtra(RESULT_KEY_SAVED_FILE_PATH, pickedImageFilePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (pickedImage == null) {
                            Toast.makeText(this, R.string.info_picked_image_deleted,
                                    Toast.LENGTH_SHORT).show();
                            setResult(Activity.RESULT_CANCELED);
                            finish();
                            return;
                        }
                    }

                    if (pickedImageFilePath != null) {
                        // thumbnail
                        try {
                            Bitmap thumbnail = ImageUtils.getResizedBitmap(this, selectedImageUri,
                                    true, thumbnailSize, thumbnailSize);
                            resultData.putExtra(RESULT_KEY_THUMBNAIL_BITMAP, thumbnail);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // preview
                        if (isNeedPreview) {
                            try {
                                Bitmap preview = ImageUtils.getResizedBitmap(this, selectedImageUri,
                                        false, previewWidth, previewHeight);
                                previewBitmap = preview;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    setResult(Activity.RESULT_OK, resultData);
                } else {
                    setResult(resultCode);
                }
                finish();
                break;
            case REQUEST_PICK_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImageUri = data.getData();

                    Bitmap pickedImage = null;
                    Intent resultData = new Intent();
                    try {
                        pickedImage = ImageUtils.getResizedBitmap(this, selectedImageUri, false,
                                storeSizeLimitation, storeSizeLimitation);
                        tmpImageFile = StorageUtils.saveCompressedImage(this, pickedImage, 100);
                        pickedImage.recycle();
                        pickedImage = null;
                        imageFileUri = Uri.fromFile(new File(tmpImageFile));
                        doCrop();
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (pickedImage == null) {
                            Toast.makeText(this, R.string.info_picked_image_deleted,
                                    Toast.LENGTH_SHORT).show();
                        }
                        setResult(Activity.RESULT_CANCELED);
                        finish();
                    }
                } else {
                    setResult(resultCode);
                    finish();
                }
                break;
            case REQUEST_TO_CROP:
                if (resultCode == Activity.RESULT_OK) {
                    Intent resultData = new Intent();
                    if (cropFilePath != null && new File(cropFilePath).exists()) {
                        resultData.putExtra(RESULT_KEY_SAVED_FILE_PATH, cropFilePath);
                        // thumbnail
                        try {
                            Bitmap thumbnail = ImageUtils.getResizedBitmap(cropFilePath, true,
                                    thumbnailSize, thumbnailSize);
                            resultData.putExtra(RESULT_KEY_THUMBNAIL_BITMAP, thumbnail);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    setResult(Activity.RESULT_OK, resultData);
                    cropFilePath = null;
                } else {
                    setResult(resultCode);
                    cropFilePath = null;
                }
                if (tmpImageFile != null) {
                    new File(tmpImageFile).delete();
                }
                finish();
                break;
        }
    }

    private void doCrop() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(
                intent, 0);
        int size = list.size();
        if (size == 0) {
            // rare condition: unable to find image crop app, resize it directly
            fallbackCropToResizeAndReturn();
            return;
        }

        intent.setData(imageFileUri);
        intent.putExtra("outputX", cropWidth);
        intent.putExtra("outputY", cropHeight);
        intent.putExtra("aspectX", cropWidth);
        intent.putExtra("aspectY", cropHeight);
        intent.putExtra("scale", true);
//        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        cropFilePath = StorageUtils.getStoredImageFilePath(this);
        File f = new File(cropFilePath);
        Uri uri = Uri.fromFile(f);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (size == 1) {
            Intent i = new Intent(intent);
            ResolveInfo res = list.get(0);
            i.setComponent(new ComponentName(res.activityInfo.packageName,
                    res.activityInfo.name));
            startActivityForResult(i, REQUEST_TO_CROP);
        } else {
            final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
            for (ResolveInfo res : list) {
                final CropOption co = new CropOption();
                co.title = getPackageManager().getApplicationLabel(
                        res.activityInfo.applicationInfo);
                co.icon = getPackageManager().getApplicationIcon(
                        res.activityInfo.applicationInfo);
                co.appIntent = new Intent(intent);
                co.appIntent.setComponent(new ComponentName(
                        res.activityInfo.packageName, res.activityInfo.name));
                cropOptions.add(co);
            }
            CropOptionAdapter adapter = new CropOptionAdapter(
                    getApplicationContext(), cropOptions);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.info_pick_app_to_do);
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    startActivityForResult(cropOptions.get(item).appIntent, REQUEST_TO_CROP);
                    dialog.dismiss();
                }
            });

            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.cancel();
                }
            });

            builder.create().show();
        }
    }

    private void fallbackCropToResizeAndReturn() {
        Bitmap bitmap = null;
        try {
            bitmap = ImageUtils.getBitmapFromUri(this, imageFileUri);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, cropWidth,
                    cropHeight, true);
            String filePath = StorageUtils.saveCompressedImage(this,
                    resized, 100);
            Intent resultData = new Intent();
            resultData.putExtra(RESULT_KEY_SAVED_FILE_PATH, filePath);
            setResult(Activity.RESULT_OK, resultData);
        } catch (Exception e) {
            e.printStackTrace();
            setResult(Activity.RESULT_CANCELED);
        } finally {
            finish();
        }
    }

    public class CropOptionAdapter extends ArrayAdapter<CropOption> {
        private ArrayList<CropOption> mOptions;
        private LayoutInflater mInflater;

        public CropOptionAdapter(Context context, ArrayList<CropOption> options) {
            super(context, R.layout.list_array_item_crop_selector, options);

            mOptions = options;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup group) {
            if (convertView == null) {
                convertView = mInflater.inflate(
                        R.layout.list_array_item_crop_selector, null);
            }

            CropOption item = mOptions.get(position);
            if (item != null) {
                ((ImageView) convertView.findViewById(R.id.iv_icon))
                        .setImageDrawable(item.icon);
                ((TextView) convertView.findViewById(R.id.tv_name))
                        .setText(item.title);
                return convertView;
            }
            return null;
        }
    }

    public class CropOption {
        public CharSequence title;
        public Drawable icon;
        public Intent appIntent;
    }
}
