package com.dengyuman.getcropimage;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

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
    public static final String RESULT_KEY_PREVIEW_BITMAP = "result_preview_bitmap";

    public static final String PARRMETER_REQUEST_TYPE = "param_type";
    public static final String PARAMETER_PREVIEW_TARGET_WIDTH = "param_target_width";
    public static final String PRRAMETER_PREVIEW_TARGET_HEIGHT = "param_target_height";
    public static final String PRRAMETER_CROP_WIDTH = "param_crop_width";
    public static final String PRRAMETER_CROP_HEIGHT = "param_crop_height";

    private static final int DEFAULT_CROP_SIZE = 300;

    private Uri imageFileUri;

    private String tmpImageFile; // tmp file should be delete before quit

    private int getImageRequestType;
    private int previewWidth;
    private int previewHeight;
    private int cropWidth;
    private int cropHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getImageRequestType = getIntent().getIntExtra(PARRMETER_REQUEST_TYPE, REQUSET_CAMERA);
        previewWidth = getIntent().getIntExtra(PARAMETER_PREVIEW_TARGET_WIDTH, -1);
        previewHeight = getIntent().getIntExtra(PRRAMETER_PREVIEW_TARGET_HEIGHT, -1);
        cropWidth = getIntent().getIntExtra(PRRAMETER_CROP_WIDTH, DEFAULT_CROP_SIZE);
        cropHeight = getIntent().getIntExtra(PRRAMETER_CROP_HEIGHT, DEFAULT_CROP_SIZE);

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
        System.out.println("============ captureCamera");
        finish();
    }

    private void captureCameraAndCrop() {
        System.out.println("============ captureCameraAndCrop");
        finish();
    }

    private void pickImage() {
        System.out.println("============ pickImage");
        finish();
    }

    private void pickImageAndCrop() {
        System.out.println("============ pickImageAndCrop");
        finish();
    }
}
