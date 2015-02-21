package com.gloomy.shreddingrobot.UIFragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.Utility.BaseFragment;
import com.gloomy.shreddingrobot.Utility.BitmapWorkerTask;
import com.gloomy.shreddingrobot.Utility.Constants;
import com.gloomy.shreddingrobot.Widget.Logger;
import com.gloomy.shreddingrobot.Widget.TypefaceTextView;

import java.io.File;

public class SettingFragment extends BaseFragment {

    private static final String TAG = "SettingFragment";
    static final int REQUEST_TAKE_PHOTO  = 1;

    private int speedUnitToggle;
    public TypefaceTextView[] speedUnits = new TypefaceTextView[3];

    private int timeUnitToggle;
    public TypefaceTextView[] timeUnits = new TypefaceTextView[2];

    private ImageView profilePhoto;
    private Uri photoUri;
    private String photoPath;
    private int profileHeight, profileWidth;

    private TypefaceTextView tvUserName;
    private EditText etUserName;
    private String userName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        findView(rootView);
        bindEvent();
        return rootView;
    }

    private void findView(View rootView){
        speedUnits[0] = (TypefaceTextView) rootView.findViewById(R.id.tv_set_to_km_per_hour);
        speedUnits[1] = (TypefaceTextView) rootView.findViewById(R.id.tv_set_to_m_per_sec);
        speedUnits[2] = (TypefaceTextView) rootView.findViewById(R.id.tv_set_to_mi_per_hour);

        timeUnits[0] = (TypefaceTextView) rootView.findViewById(R.id.tv_set_to_sec);
        timeUnits[1] = (TypefaceTextView) rootView.findViewById(R.id.tv_set_to_mill_sec);

        profilePhoto = (ImageView) rootView.findViewById(R.id.iv_profile_photo);
        tvUserName = (TypefaceTextView) rootView.findViewById(R.id.tv_profile_name);
        etUserName = (EditText) rootView.findViewById(R.id.et_profile_name);
    }

    private void bindEvent() {
        profilePhoto.setOnLongClickListener(profilePhotoOnLongClickListener);
        profilePhoto.setOnClickListener(profilePhotoOnClickListener);

        etUserName.setOnEditorActionListener(userNameEditorActionListener);
        tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvUserName.setVisibility(View.GONE);
                etUserName.setVisibility(View.VISIBLE);
                etUserName.requestFocus();
                showKeyboard(etUserName);
            }
        });

        for (TypefaceTextView speedUnit : speedUnits) {
            speedUnit.setOnClickListener(speedUnitOnClickListener);
        }

        for (TypefaceTextView timeUnit : timeUnits) {
            timeUnit.setOnClickListener(timeUnitOnClickListener);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadProfileImage();

        userName = sp.getString(Constants.SP_USER_NAME, "");
        if (!userName.isEmpty()) {
            tvUserName.setText(userName);
            etUserName.setHint(userName);
        }

        speedUnitToggle = sp.getInt(Constants.SP_SPEED_UNIT, 0);
        timeUnitToggle = sp.getInt(Constants.SP_TIME_UNIT, 0);

        for (int i = 0; i < speedUnits.length; i++){
            if (speedUnitToggle == i){
                speedUnits[i].setVisibility(View.VISIBLE);
            } else {
                speedUnits[i].setVisibility(View.GONE);
            }
        }

        for (int i = 0; i < timeUnits.length; i++){
            if (timeUnitToggle == i){
                timeUnits[i].setVisibility(View.VISIBLE);
            } else {
                timeUnits[i].setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (tvUserName.getVisibility()==View.GONE) {
            updateUserName();
        }
    }

    private View.OnClickListener speedUnitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int oldOp, newOp;
            oldOp = speedUnitToggle;
            switch (oldOp){
                default:
                    newOp = 1;
                    break;
                case 1:
                    newOp = 2;
                    break;
                case 2:
                    newOp = 0;
                    break;
            }
            AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(parentActivity,
                    R.animator.card_flip_top_out);
            flipOut.setTarget(speedUnits[oldOp]);
            flipOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    for (TypefaceTextView speedUnit : speedUnits) {
                        speedUnit.setEnabled(false);
                    }
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    for (TypefaceTextView speedUnit : speedUnits) {
                        speedUnit.setEnabled(true);
                    }
                    speedUnits[oldOp].setVisibility(View.GONE);
                }
            });
            flipOut.start();

            AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(parentActivity,
                    R.animator.card_flip_top_in);
            speedUnits[newOp].setVisibility(View.VISIBLE);
            flipIn.setTarget(speedUnits[newOp]);
            sp.edit().putInt(Constants.SP_SPEED_UNIT, newOp).apply();
            speedUnitToggle = newOp;

            flipIn.start();
        }
    };

    private View.OnClickListener timeUnitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int oldOp, newOp;
            oldOp = timeUnitToggle;
            switch (oldOp){
                default:
                    newOp = 1;
                    break;
                case 1:
                    newOp = 0;
                    break;
            }
            AnimatorSet flipOut = (AnimatorSet) AnimatorInflater.loadAnimator(parentActivity,
                    R.animator.card_flip_right_out);
            flipOut.setTarget(timeUnits[oldOp]);
            flipOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    for (TypefaceTextView timeUnit : timeUnits) {
                        timeUnit.setEnabled(false);
                    }
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    for (TypefaceTextView timeUnit : timeUnits) {
                        timeUnit.setEnabled(true);
                    }
                    timeUnits[oldOp].setVisibility(View.GONE);
                }
            });
            flipOut.start();

            AnimatorSet flipIn = (AnimatorSet) AnimatorInflater.loadAnimator(parentActivity,
                    R.animator.card_flip_right_in);
            timeUnits[newOp].setVisibility(View.VISIBLE);
            flipIn.setTarget(timeUnits[newOp]);
            sp.edit().putInt(Constants.SP_TIME_UNIT, newOp).apply();
            timeUnitToggle = newOp;

            flipIn.start();
        }
    };

    private View.OnLongClickListener profilePhotoOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            photoPath = sp.getString(Constants.SP_PROFILE_PHOTO_PATH, null);
            if (photoPath == null) {
                return false;
            } else {
                profilePhoto.setImageResource(R.drawable.profile_placeholder);
                sp.edit().remove(Constants.SP_PROFILE_PHOTO_PATH).apply();
                return true;
            }
        }
    };

    private TextView.OnEditorActionListener userNameEditorActionListener =  new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                updateUserName();
            }
            return false;
        }
    };

    private void updateUserName() {
        String tempUserName = etUserName.getText().toString().trim();
        if (!tempUserName.isEmpty()){
            userName = tempUserName;
            sp.edit().putString(Constants.SP_USER_NAME, userName).apply();
            tvUserName.setText(userName);
            etUserName.setHint(userName);
        }

        hideKeyboard(etUserName);
        etUserName.setVisibility(View.GONE);
        tvUserName.setVisibility(View.VISIBLE);
    }

    private View.OnClickListener profilePhotoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // create Intent to take a picture and return control to the calling application
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (intent.resolveActivity(parentActivity.getPackageManager()) != null) {
                // Create a file, to which the photo saves
                photoUri = createImageFileUri();

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            } else {
                Logger.e(TAG, "Failed to create camera intent");
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                profilePhoto.setImageResource(R.drawable.profile_loading);

                photoPath = photoUri.getPath();
                BitmapWorkerTask task = new BitmapWorkerTask(profilePhoto, photoPath, profileHeight, profileWidth);
                task.execute();

                sp.edit().putString(Constants.SP_PROFILE_PHOTO_PATH, photoPath).apply();
            }
        }
    }

    private Uri createImageFileUri() {
        // Create an image file name[
        String imageFileName = "profile";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return Uri.fromFile(new File (storageDir, imageFileName));
    }

    private void loadProfileImage() {
        photoPath = sp.getString(Constants.SP_PROFILE_PHOTO_PATH, null);
        profileHeight = (int) getResources().getDimension(R.dimen.profile_photo_size);
        profileWidth = (int) getResources().getDimension(R.dimen.profile_photo_size);

        if (photoPath!=null) {
            BitmapWorkerTask task = new BitmapWorkerTask(profilePhoto, photoPath, profileHeight, profileWidth);
            task.execute();
        }// else: Default placeholder will be shown
    }
}