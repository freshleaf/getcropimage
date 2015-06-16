package com.dengyuman.getcropimage.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.dengyuman.getcropimage.GetImageUtilsActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnTakePicture;
    private Button btnTakePictureAndCrop;
    private Button btnPickPicture;
    private Button btnPickPictureAndCrop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
        btnTakePictureAndCrop = (Button) findViewById(R.id.btnTakePictureAndCrop);
        btnPickPicture = (Button) findViewById(R.id.btnPickPicture);
        btnPickPictureAndCrop = (Button) findViewById(R.id.btnPickPictureAndCrop);

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
        switch (v.getId()) {
            case R.id.btnTakePicture:
                Intent intent = new Intent(this, GetImageUtilsActivity.class);
                intent.putExtra(GetImageUtilsActivity.PARRMETER_REQUEST_TYPE,
                        GetImageUtilsActivity.REQUSET_CAMERA);
                startActivityForResult(intent, GetImageUtilsActivity.REQUSET_CAMERA);
                break;
            case R.id.btnTakePictureAndCrop:
                intent = new Intent(this, GetImageUtilsActivity.class);
                intent.putExtra(GetImageUtilsActivity.PARRMETER_REQUEST_TYPE,
                        GetImageUtilsActivity.REQUEST_CAMERA_CROP);
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
}
