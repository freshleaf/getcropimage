package com.dengyuman.getcropimage.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dengyuman.getcropimage.GetImageUtilsActivity;

import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnTakePicture;
    private Button btnTakePictureAndCrop;
    private Button btnPickPicture;
    private Button btnPickPictureAndCrop;
    private TextView tvImageFile;
    private TextView tvResultInfo;
    private ImageView ivResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
        btnTakePictureAndCrop = (Button) findViewById(R.id.btnTakePictureAndCrop);
        btnPickPicture = (Button) findViewById(R.id.btnPickPicture);
        btnPickPictureAndCrop = (Button) findViewById(R.id.btnPickPictureAndCrop);
        ivResult = (ImageView) findViewById(R.id.ivResult);
        tvResultInfo = (TextView) findViewById(R.id.tvResultInfo);
        tvImageFile = (TextView) findViewById(R.id.tvImageFile);

        btnTakePicture.setOnClickListener(this);
        btnTakePictureAndCrop.setOnClickListener(this);
        btnPickPicture.setOnClickListener(this);
        btnPickPictureAndCrop.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        ivResult.setVisibility(View.GONE);
        tvResultInfo.setVisibility(View.GONE);
        tvImageFile.setText("");
        switch (v.getId()) {
            case R.id.btnTakePicture:
                Intent intent = new Intent(this, GetImageUtilsActivity.class);
                intent.putExtra(GetImageUtilsActivity.PARRMETER_REQUEST_TYPE,
                        GetImageUtilsActivity.REQUSET_CAMERA);
                // set preview image size desire size, otherwise it will not return preview bitmap
                intent.putExtra(GetImageUtilsActivity.PARAMETER_PREVIEW_WIDTH, 800);
                intent.putExtra(GetImageUtilsActivity.PARAMETER_PREVIEW_HEIGHT, 800);
                startActivityForResult(intent, GetImageUtilsActivity.REQUSET_CAMERA);
                break;
            case R.id.btnTakePictureAndCrop:
                intent = new Intent(this, GetImageUtilsActivity.class);
                intent.putExtra(GetImageUtilsActivity.PARRMETER_REQUEST_TYPE,
                        GetImageUtilsActivity.REQUEST_CAMERA_CROP);
                intent.putExtra(GetImageUtilsActivity.PRRAMETER_CROP_WIDTH, 400);
                intent.putExtra(GetImageUtilsActivity.PRRAMETER_CROP_HEIGHT, 600);
                startActivityForResult(intent, GetImageUtilsActivity.REQUEST_CAMERA_CROP);
                break;
            case R.id.btnPickPicture:
                intent = new Intent(this, GetImageUtilsActivity.class);
                intent.putExtra(GetImageUtilsActivity.PARRMETER_REQUEST_TYPE,
                        GetImageUtilsActivity.REQUEST_PICK);
                startActivityForResult(intent, GetImageUtilsActivity.REQUEST_PICK);
                break;
            case R.id.btnPickPictureAndCrop:
                intent = new Intent(this, GetImageUtilsActivity.class);
                intent.putExtra(GetImageUtilsActivity.PARRMETER_REQUEST_TYPE,
                        GetImageUtilsActivity.REQUEST_PICK_CROP);
                startActivityForResult(intent, GetImageUtilsActivity.REQUEST_PICK_CROP);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case GetImageUtilsActivity.REQUSET_CAMERA:
                Bitmap previewImage = GetImageUtilsActivity.getPreviewBitmap();
                ivResult.setImageBitmap(previewImage);
                ivResult.setVisibility(View.VISIBLE);
                tvResultInfo.setText("preview image: " + previewImage.getWidth() + "x" + previewImage.getHeight());
                tvResultInfo.setVisibility(View.VISIBLE);
                String imageFilePath = data.getStringExtra(GetImageUtilsActivity.RESULT_KEY_SAVED_FILE_PATH);
                tvImageFile.setText(imageFilePath);

                // clear file
                File newFile = new File(imageFilePath);
                if (newFile.exists()) {
                    newFile.delete();
                }
                break;
            case GetImageUtilsActivity.REQUEST_CAMERA_CROP:
                imageFilePath = data.getStringExtra(GetImageUtilsActivity.RESULT_KEY_SAVED_FILE_PATH);
                Bitmap crop = BitmapFactory.decodeFile(imageFilePath);
                ivResult.setImageBitmap(crop);
                ivResult.setVisibility(View.VISIBLE);
                tvResultInfo.setText("cropped image: " + crop.getWidth() + "x" + crop.getHeight());
                tvResultInfo.setVisibility(View.VISIBLE);
                tvImageFile.setText(imageFilePath);

                // clear file
                newFile = new File(imageFilePath);
                if (newFile.exists()) {
                    newFile.delete();
                }
                break;
            case GetImageUtilsActivity.REQUEST_PICK:
                Bitmap thumbnail = (Bitmap) data.getParcelableExtra(GetImageUtilsActivity.RESULT_KEY_THUMBNAIL_BITMAP);
                ivResult.setImageBitmap(thumbnail);
                ivResult.setVisibility(View.VISIBLE);
                tvResultInfo.setText("picked image thumbnail: " + thumbnail.getWidth() + "x" + thumbnail.getHeight());
                tvResultInfo.setVisibility(View.VISIBLE);
                imageFilePath = data.getStringExtra(GetImageUtilsActivity.RESULT_KEY_SAVED_FILE_PATH);
                tvImageFile.setText(imageFilePath);

                // clear file
                newFile = new File(imageFilePath);
                if (newFile.exists()) {
                    newFile.delete();
                }
                break;
            case GetImageUtilsActivity.REQUEST_PICK_CROP:
                imageFilePath = data.getStringExtra(GetImageUtilsActivity.RESULT_KEY_SAVED_FILE_PATH);
                Bitmap cropThumbnail = (Bitmap) data.getParcelableExtra(GetImageUtilsActivity.RESULT_KEY_THUMBNAIL_BITMAP);
                ivResult.setImageBitmap(cropThumbnail);
                ivResult.setVisibility(View.VISIBLE);
                tvResultInfo.setText("cropped thumbnail image: " + cropThumbnail.getWidth() + "x" + cropThumbnail.getHeight());
                tvResultInfo.setVisibility(View.VISIBLE);
                imageFilePath = data.getStringExtra(GetImageUtilsActivity.RESULT_KEY_SAVED_FILE_PATH);
                tvImageFile.setText(imageFilePath);

                // clear file
                newFile = new File(imageFilePath);
                if (newFile.exists()) {
                    newFile.delete();
                }
                break;
        }
    }
}
