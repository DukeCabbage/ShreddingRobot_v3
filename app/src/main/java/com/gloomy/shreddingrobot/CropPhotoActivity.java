package com.gloomy.shreddingrobot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gloomy.shreddingrobot.Utility.BitmapWorkerTask;
import com.gloomy.shreddingrobot.Utility.Constants;

import java.io.File;


public class CropPhotoActivity extends Activity implements View.OnTouchListener {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private ImageView profilePhoto;
    private Uri photoUri;
    private String photoPath;
    private int profileHeight, profileWidth;



    private int _xDelta;
    private int _yDelta;
    private ViewGroup _root;
    private View scaleWindow;
    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_photo);

        profilePhoto = (ImageView) findViewById(R.id.iv_photo);
        scaleWindow = findViewById(R.id.scaleWindow);
        _root = (ViewGroup) findViewById(R.id.root);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 150);
        scaleWindow.setLayoutParams(layoutParams);
        scaleWindow.setOnTouchListener(this);
        sp = getSharedPreferences("ShreddingPref", Context.MODE_PRIVATE);




        // create Intent to take a picture and return control to the calling application
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent pickPicIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        String pickTitle = "Select or take a new Picture";
        Intent chooserIntent = Intent.createChooser(pickPicIntent, pickTitle);
        chooserIntent.putExtra
                (
                        Intent.EXTRA_INITIAL_INTENTS,
                        new Intent[]{takePhotoIntent}
                );

        // Create a file, to which the photo saves
        photoUri = createImageFileUri();


        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);


        startActivityForResult(chooserIntent, REQUEST_TAKE_PHOTO);





    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {

                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

//                // Image captured and saved to fileUri specified in the Intent
                profilePhoto.setImageResource(R.drawable.profile_loading);
                if (isCamera) {


                    photoPath = photoUri.getPath();
                    BitmapWorkerTask task = new BitmapWorkerTask(profilePhoto, photoPath, profileHeight, profileWidth);
                    task.execute();


                    sp.edit().putString(Constants.SP_PROFILE_PHOTO_PATH, photoPath).apply();
                    loadProfileImage();



                } else {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    photoPath = picturePath;
                    cursor.close();
                    BitmapWorkerTask task = new BitmapWorkerTask(profilePhoto, photoPath, profileHeight, profileWidth);
                    task.execute();

                    sp.edit().putString(Constants.SP_PROFILE_PHOTO_PATH, photoPath).apply();
                    loadProfileImage();

                }


            }
        }
    }

    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = X - _xDelta;
                layoutParams.topMargin = Y - _yDelta;
                layoutParams.rightMargin = -250;
                layoutParams.bottomMargin = -250;
                view.setLayoutParams(layoutParams);
                break;
        }
        _root.invalidate();
        return true;
    }
    private Uri createImageFileUri() {
        // Create an image file name[
        String imageFileName = "profile";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return Uri.fromFile(new File(storageDir, imageFileName));
    }

    private void loadProfileImage() {
        photoPath = sp.getString(Constants.SP_PROFILE_PHOTO_PATH, null);
        profileHeight = (int) getResources().getDimension(R.dimen.profile_photo_size);
        profileWidth = (int) getResources().getDimension(R.dimen.profile_photo_size);

        if (photoPath != null) {
            BitmapWorkerTask task = new BitmapWorkerTask(profilePhoto, photoPath, profileHeight, profileWidth);
            task.execute();

        }// else: Default placeholder will be shown
    }

}
