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
import com.gloomy.shreddingrobot.Widget.TypefaceTextView;

import java.io.File;


public class CropPhotoActivity extends Activity implements View.OnTouchListener {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private ImageView profilePhoto;
    private Uri photoUri;
    private String photoPath;
    private int profileHeight, profileWidth;
    private TypefaceTextView check_btn, close_btn;


    private int _xDelta;
    private int _yDelta;
    private ViewGroup _root;
    private View scaleWindow;
    SharedPreferences sp;

//    private static final String TAG = "Touch";
//    @SuppressWarnings("unused")
//    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;
//
//    // These matrices will be used to scale points of the image
//    Matrix matrix = new Matrix();
//    Matrix savedMatrix = new Matrix();
//
//    // The 3 states (events) which the user is trying to perform
//    static final int NONE = 0;
//    static final int DRAG = 1;
//    static final int ZOOM = 2;
//    int mode = NONE;
//
//    // these PointF objects are used to record the point(s) the user is touching
//    PointF start = new PointF();
//    PointF mid = new PointF();
//    float oldDist = 1f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_photo);

        check_btn = (TypefaceTextView) findViewById(R.id.check_btn);
        check_btn.setText(Constants.ICON_Check);
        close_btn = (TypefaceTextView) findViewById(R.id.close_btn);
        close_btn.setText(Constants.ICON_Close);
       profilePhoto = (ImageView) findViewById(R.id.iv_photo);
//        profilePhoto.setOnTouchListener(this);
        scaleWindow = findViewById(R.id.scaleWindow);
        _root = (ViewGroup) findViewById(R.id.root);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(300, 300);

        scaleWindow.setLayoutParams(layoutParams);
        scaleWindow.setOnTouchListener(this);
        sp = getSharedPreferences("ShreddingPref", Context.MODE_PRIVATE);



                if(sp.getBoolean("CROP_OPTION", true)) {
                    // create Intent to take a picture and return control to the calling application
                    Intent pickPicIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // Create a file, to which the photo saves
                    photoUri = createImageFileUri();
                    pickPicIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(pickPicIntent, REQUEST_TAKE_PHOTO);
                    bindEvent();
                }
        else{
                    Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Create a file, to which the photo saves
                    photoUri = createImageFileUri();
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePhotoIntent, REQUEST_TAKE_PHOTO);
                    bindEvent();
                }

    }

    private  void bindEvent(){
        check_btn.setOnClickListener(checkBtnOnClickListener);
        close_btn.setOnClickListener(closeBtnOnClickListener);
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
                    BitmapWorkerTask task = new BitmapWorkerTask(profilePhoto, photoPath, profileHeight, profileWidth,0,0);
                    task.execute();


                    sp.edit().putString(Constants.SP_PROFILE_PHOTO_PATH, photoPath).apply();
                    loadProfileImage();
                    scaleWindow.setVisibility(View.VISIBLE);


                } else {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    photoPath = picturePath;
                    cursor.close();
                    BitmapWorkerTask task = new BitmapWorkerTask(profilePhoto, photoPath, profileHeight, profileWidth,0,0);
                    task.execute();

                    sp.edit().putString(Constants.SP_PROFILE_PHOTO_PATH, photoPath).apply();
                    loadProfileImage();
                    scaleWindow.setVisibility(View.VISIBLE);
                }


            }
        }
    }


    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
//        ImageView profilePhoto = (ImageView) view;
//        profilePhoto.setScaleType(ImageView.ScaleType.MATRIX);
//        float scale;
//
//        dumpEvent(event);
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

//        switch (event.getAction() & MotionEvent.ACTION_MASK)
//        {
//            case MotionEvent.ACTION_DOWN:   // first finger down only
//                savedMatrix.set(matrix);
//                start.set(event.getX(), event.getY());
//                Log.d(TAG, "mode=DRAG"); // write to LogCat
//                mode = DRAG;
//                break;
//
//            case MotionEvent.ACTION_UP: // first finger lifted
//
//            case MotionEvent.ACTION_POINTER_UP: // second finger lifted
//
//                mode = NONE;
//                Log.d(TAG, "mode=NONE");
//                break;
//
//            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down
//
//                oldDist = spacing(event);
//                Log.d(TAG, "oldDist=" + oldDist);
//                if (oldDist > 5f) {
//                    savedMatrix.set(matrix);
//                    midPoint(mid, event);
//                    mode = ZOOM;
//                    Log.d(TAG, "mode=ZOOM");
//                }
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//
//                if (mode == DRAG)
//                {
//                    matrix.set(savedMatrix);
//                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y); // create the transformation in the matrix  of points
//                }
//                else if (mode == ZOOM)
//                {
//                    // pinch zooming
//                    float newDist = spacing(event);
//                    Log.d(TAG, "newDist=" + newDist);
//                    if (newDist > 5f)
//                    {
//                        matrix.set(savedMatrix);
//                        scale = newDist / oldDist; // setting the scaling of the
//                        // matrix...if scale > 1 means
//                        // zoom in...if scale < 1 means
//                        // zoom out
//                        matrix.postScale(scale, scale, mid.x, mid.y);
//                    }
//                }
//                break;
//        }
//
//        profilePhoto.setImageMatrix(matrix);
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
        int c_x = sp.getInt("CROP_X", 0);
        int c_y = sp.getInt("CROP_Y", 0);
        if (photoPath != null) {
            BitmapWorkerTask task = new BitmapWorkerTask(profilePhoto, photoPath, profileHeight, profileWidth, c_x, c_y);
            task.execute();

        }// else: Default placeholder will be shown
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
            finish();


        }
    };

    private View.OnClickListener closeBtnOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            finish();


        }
    };


//    private float spacing(MotionEvent event)
//    {
//        float x = event.getX(0) - event.getX(1);
//        float y = event.getY(0) - event.getY(1);
//        return FloatMath.sqrt(x * x + y * y);
//    }


//
//    private void midPoint(PointF point, MotionEvent event)
//    {
//        float x = event.getX(0) + event.getX(1);
//        float y = event.getY(0) + event.getY(1);
//        point.set(x / 2, y / 2);
//    }
//
//    /** Show an event in the LogCat view, for debugging */
//    private void dumpEvent(MotionEvent event)
//    {
//        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE","POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
//        StringBuilder sb = new StringBuilder();
//        int action = event.getAction();
//        int actionCode = action & MotionEvent.ACTION_MASK;
//        sb.append("event ACTION_").append(names[actionCode]);
//
//        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP)
//        {
//            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
//            sb.append(")");
//        }
//
//        sb.append("[");
//        for (int i = 0; i < event.getPointerCount(); i++)
//        {
//            sb.append("#").append(i);
//            sb.append("(pid ").append(event.getPointerId(i));
//            sb.append(")=").append((int) event.getX(i));
//            sb.append(",").append((int) event.getY(i));
//            if (i + 1 < event.getPointerCount())
//                sb.append(";");
//        }
//
//        sb.append("]");
//        Log.d("Touch Events ---------", sb.toString());
//    }
}
