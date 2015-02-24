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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
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
    private static final int REQUEST_TAKE_PHOTO  = 1;
    public static final int MAX_SLEEP_SPAN = 60;
    public static final String SLEEP_TIME_UNIT = " min";

    private int speedUnitToggle;
    private TypefaceTextView[] speedUnits = new TypefaceTextView[3];

    private int timeUnitToggle;
    private TypefaceTextView[] timeUnits = new TypefaceTextView[2];

    private ImageView profilePhoto;
    private Uri photoUri;
    private String photoPath;
    private int profileHeight, profileWidth;

    private TypefaceTextView tvUserName;
    private EditText etUserName;
    private String userName;

    private SeekBar sleepTimerBar;
    private TypefaceTextView tv_sleepTimerLabel;
    private String sleepLabel, sleepLabel_alt;
    private int sleepTime;

    private Switch liftSwitch;
    private boolean liftOff;

    private static final int SELECT_PICTURE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        findView(rootView);
        bindEvent();

        sleepLabel = getResources().getString(R.string.auto_off_lift_label) + " ";
        sleepLabel_alt = getResources().getString(R.string.auto_off_timer_label_zero);
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

        tv_sleepTimerLabel = (TypefaceTextView) rootView.findViewById(R.id.tv_auto_off_timer_label);
        sleepTimerBar = (SeekBar) rootView.findViewById(R.id.sleep_timer_bar);
        sleepTimerBar.setMax(MAX_SLEEP_SPAN);
        liftSwitch = (Switch) rootView.findViewById(R.id.lift_auto_off_switch);
    }

    private void bindEvent() {
        profilePhoto.setOnLongClickListener(profilePhotoOnLongClickListener);
        profilePhoto.setOnClickListener(profilePhotoOnClickListener);

        etUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    updateUserName();
                }
                return false;
            }
        });

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

        sleepTimerBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress==0){
                    tv_sleepTimerLabel.setText(sleepLabel_alt);
                }else{
                    tv_sleepTimerLabel.setText(sleepLabel + progress + SLEEP_TIME_UNIT);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sleepTime = seekBar.getProgress();
                sp.edit().putInt(Constants.SP_SLEEP_TIME, sleepTime).apply();
            }
        });

        liftSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(Constants.SP_LIFT_OFF, isChecked).apply();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        loadProfileImage();

        if(parentActivity.isTracking()) {
            sleepTimerBar.setEnabled(false);
            Logger.e(TAG, ""+sleepTimerBar.getVisibility());
//            liftSwitch.setEnabled(false);
        }else{
            sleepTimerBar.setEnabled(true);
            Logger.e(TAG, ""+sleepTimerBar.getVisibility());
//            liftSwitch.setEnabled(true);
        }

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

        sleepTime = sp.getInt(Constants.SP_SLEEP_TIME, 0);
        sleepTimerBar.setProgress(sleepTime);

        if (sleepTime==0){
            tv_sleepTimerLabel.setText(sleepLabel_alt);
        }else{
            tv_sleepTimerLabel.setText(sleepLabel + sleepTime + SLEEP_TIME_UNIT);
        }

        liftOff = sp.getBoolean(Constants.SP_LIFT_OFF, false);
        liftSwitch.setChecked(liftOff);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (tvUserName.getVisibility()==View.GONE) {
            updateUserName();
        }
        parentActivity.settingUpdated();
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