#getcropimage the Android aar

Get crop image AAR is the lib to call Android `"com.android.camera.action.CROP"` to crop image by taking a phote or picking a picture.

## How to use
### Add lib dependence
* in `Android Studio`: add `compile 'com.dengyuman:gercropimage:1.0@aar'` in build.gradle<br>
or<br>
* download and add yourself: [github release v1.0](https://github.com/freshleaf/getcropimage/releases/download/v1.0/getcropimage-1.0.aar.zip)

### Usage
There are 4 way to get picture:<br>
>1. take picture: Use camera to take a new picture, keep the ratio but resize to make it smaller and save it as file, return the file path, a thumbnail image and a possible preview image
>2. take picture and crop: Use camera to take a new picture and crop, return crop image file path, thumbnail image
>3. pick an image: Use Android native image picker to pick one picture, resize the selected image and return the file path, along with a thumbnail image and a possible preview image
>4. pick an image and crop: Use Android native image picker to pick and crop, return crop image file path, thumbnail image

#### take picture
* request
```Java
      Intent intent = new Intent(this, GetImageUtilsActivity.class);
      intent.putExtra(GetImageUtilsActivity.PARRMETER_REQUEST_TYPE, GetImageUtilsActivity.REQUSET_CAMERA);
      // set preview image size desire size, otherwise it will not return preview bitmap
      intent.putExtra(GetImageUtilsActivity.PARAMETER_PREVIEW_WIDTH, 800); // optional
      intent.putExtra(GetImageUtilsActivity.PARAMETER_PREVIEW_HEIGHT, 800); // optional
      startActivityForResult(intent, GetImageUtilsActivity.REQUSET_CAMERA);
```
* response: in onActivityResult()
```Java
      String imageFilePath = data.getStringExtra(GetImageUtilsActivity.RESULT_KEY_SAVED_FILE_PATH);
      // if request has preview image width/height
      Bitmap previewImage = GetImageUtilsActivity.getPreviewBitmap();
      Bitmap thumbnail = (Bitmap) data.getParcelableExtra(GetImageUtilsActivity.RESULT_KEY_THUMBNAIL_BITMAP);
```

#### take picture and crop
* request
```Java
      intent = new Intent(this, GetImageUtilsActivity.class);
      intent.putExtra(GetImageUtilsActivity.PARRMETER_REQUEST_TYPE, GetImageUtilsActivity.REQUEST_CAMERA_CROP);
      intent.putExtra(GetImageUtilsActivity.PRRAMETER_CROP_WIDTH, 400); // optional, default is 300
      intent.putExtra(GetImageUtilsActivity.PRRAMETER_CROP_HEIGHT, 600); // optional, default is 300
      startActivityForResult(intent, GetImageUtilsActivity.REQUEST_CAMERA_CROP);
```
* response: in onActivityResult()
```Java
      String cropImageFilePath = data.getStringExtra(GetImageUtilsActivity.RESULT_KEY_SAVED_FILE_PATH);
      Bitmap thumbnail = (Bitmap) data.getParcelableExtra(GetImageUtilsActivity.RESULT_KEY_THUMBNAIL_BITMAP);
```

#### pick an image
* request
```Java
      intent = new Intent(this, GetImageUtilsActivity.class);
      intent.putExtra(GetImageUtilsActivity.PARRMETER_REQUEST_TYPE, GetImageUtilsActivity.REQUEST_PICK);
      // set preview image size desire size, otherwise it will not return preview bitmap
      intent.putExtra(GetImageUtilsActivity.PARAMETER_PREVIEW_WIDTH, 800); // optional
      intent.putExtra(GetImageUtilsActivity.PARAMETER_PREVIEW_HEIGHT, 800); // optional
      startActivityForResult(intent, GetImageUtilsActivity.REQUEST_PICK);
```
* response: in onActivityResult()
```Java
      String imageFilePath = data.getStringExtra(GetImageUtilsActivity.RESULT_KEY_SAVED_FILE_PATH);
      // if request has preview image width/height
      Bitmap previewImage = GetImageUtilsActivity.getPreviewBitmap();
      Bitmap thumbnail = (Bitmap) data.getParcelableExtra(GetImageUtilsActivity.RESULT_KEY_THUMBNAIL_BITMAP);
```

#### pick an image and crop
* request
```Java
      intent = new Intent(this, GetImageUtilsActivity.class);
      intent.putExtra(GetImageUtilsActivity.PARRMETER_REQUEST_TYPE, GetImageUtilsActivity.REQUEST_PICK_CROP);
      intent.putExtra(GetImageUtilsActivity.PRRAMETER_CROP_WIDTH, 300); // optional, default is 300
      intent.putExtra(GetImageUtilsActivity.PRRAMETER_CROP_HEIGHT, 300); // optional, default is 300
      startActivityForResult(intent, GetImageUtilsActivity.REQUEST_PICK_CROP);
```
* response: in onActivityResult()
```Java
      String cropImageFilePath = data.getStringExtra(GetImageUtilsActivity.RESULT_KEY_SAVED_FILE_PATH);
      Bitmap thumbnail = (Bitmap) data.getParcelableExtra(GetImageUtilsActivity.RESULT_KEY_THUMBNAIL_BITMAP);
```
