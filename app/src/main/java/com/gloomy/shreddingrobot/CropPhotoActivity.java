package com.gloomy.shreddingrobot;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.gloomy.shreddingrobot.Utility.BitmapWorkerTask;
import com.gloomy.shreddingrobot.Utility.Constants;
import com.gloomy.shreddingrobot.Widget.Logger;
import com.gloomy.shreddingrobot.Widget.TypefaceTextView;

import java.io.File;


public class CropPhotoActivity extends Activity implements View.OnTouchListener {
    private final static String TAG = CropPhotoActivity.class.getSimpleName();
    private static final int REQUEST_TAKE_PHOTO = 1;
    private ImageView profilePhoto;
    private Uri photoUri;
    private String photoPath;
    private TypefaceTextView check_btn, close_btn;

    private int _xDelta;
    private int _yDelta;
    private ViewGroup _root;
    private View scaleWindow;
    SharedPreferences sp;

    final int PIC_CROP = 1;

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    String savedItemClicked;

    private int photoHeight, photoWidth;
    private boolean noPhotoChosen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_photo);
        View _root = getWindow().getDecorView().findViewById(android.R.id.content);

        check_btn = (TypefaceTextView) findViewById(R.id.check_btn);
        check_btn.setText(Constants.ICON_Check);

        close_btn = (TypefaceTextView) findViewById(R.id.close_btn);
        close_btn.setText(Constants.ICON_Close);

        profilePhoto = (ImageView) findViewById(R.id.iv_raw_photo);
        profilePhoto.setOnTouchListener(this);

        scaleWindow = findViewById(R.id.scaleWindow);
        noPhotoChosen = true;
        bindEvent();
    }

    private void getPhotoMethods(boolean gallery){
        if (gallery) {
            // create Intent to take a picture and return control to the calling application
            Intent pickPicIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Create a file, to which the photo saves
            photoUri = createImageFileUri();
            pickPicIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(pickPicIntent, REQUEST_TAKE_PHOTO);
        } else {
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Create a file, to which the photo saves
            photoUri = createImageFileUri();
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (noPhotoChosen) {
            profilePhoto.post(new Runnable() {
                @Override
                public void run() {
                    photoWidth = (int) (profilePhoto.getWidth());
                    photoHeight = (int) (profilePhoto.getHeight());
                  //  profilePhoto.setPadding((int )(photoWidth*0.125), (int) (photoHeight*0.125), (int) (photoWidth*0.125), (int) (photoHeight*0.125));

                    sp = getSharedPreferences("ShreddingPref", Context.MODE_PRIVATE);
                    getPhotoMethods(sp.getBoolean("CROP_OPTION", true));
                }
            });
        }
    }

    private void bindEvent() {
        check_btn.setOnClickListener(checkBtnOnClickListener);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.d(TAG, "cropping cancelled");
                finish();
            }
        });
    }
    private void performCrop(Uri picUri) {
        try {

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
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

                // Image captured and saved to fileUri specified in the Intent
                profilePhoto.setImageResource(R.drawable.profile_loading);
                if (isCamera) {
                    photoPath = photoUri.getPath();
                } else {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    photoPath = cursor.getString(columnIndex);
                    cursor.close();
                }

                noPhotoChosen = false;
                sp.edit().putString(Constants.SP_PROFILE_PHOTO_PATH, photoPath).apply();
                loadProfileImage();
                scaleWindow.setVisibility(View.VISIBLE);
            }
        }
        else if (requestCode == PIC_CROP) {
            if (data != null) {
                // get the returned data
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap selectedBitmap = extras.getParcelable("data");

                profilePhoto.setImageBitmap(selectedBitmap);
            }
        }
    }



    private Uri createImageFileUri() {
        // Create an image file name
        String imageFileName = "profile";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return Uri.fromFile(new File(storageDir, imageFileName));
    }

    private void loadProfileImage() {
        photoPath = sp.getString(Constants.SP_PROFILE_PHOTO_PATH, null);

//        photoHeight = sp.getInt("CROP_X", 0);
//        photoWidth = sp.getInt("CROP_Y", 0);
        if (photoPath != null) {
            BitmapWorkerTask task = new BitmapWorkerTask(profilePhoto, photoPath, photoHeight, photoWidth);
            task.execute();
        }// else: Default placeholder will be shown
    }

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        dumpEvent(event);

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()  - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix);
        return true;
    }

    private View.OnClickListener checkBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int[] locations = new int[2];
            scaleWindow.getLocationOnScreen(locations);
            int x = locations[0];
            int y = locations[1];
            sp.edit().putInt("CROP_X", x).apply();
            sp.edit().putInt("CROP_Y", y).apply();
            performCrop(photoUri);
            finish();


        }
    };

    private void dumpEvent(MotionEvent event) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }
        sb.append("]");
        ;
    }

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

}
