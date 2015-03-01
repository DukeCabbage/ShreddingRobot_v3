package com.gloomy.shreddingrobot.UIFragment;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import com.andexert.library.RippleView;
import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.Utility.BaseFragment;
import com.gloomy.shreddingrobot.Utility.Constants;
import com.gloomy.shreddingrobot.Widget.Logger;
import com.gloomy.shreddingrobot.Widget.RoundedImageView;
import com.gloomy.shreddingrobot.Widget.TypefaceTextView;

import java.text.DecimalFormat;

public class ResultFragment extends BaseFragment {
    private static final String TAG = ResultFragment.class.getSimpleName();
    private static final Integer ANIMATION_STEPS = 100;
    private static final Integer ANIMATION_STEP_TIME = 33;
    private static final Integer NUMBER_OF_BARS = 4;
    private static final DecimalFormat sig3 = new DecimalFormat("@@@");
    private static final DecimalFormat sig2 = new DecimalFormat("@@");

    private static final Double CAP_MAX_SPEED = 35.0;
    private static final Double CAP_AVG_SPEED = 20.0;
    private static final Double CAP_AIR_TIME = 5.0;
    private static final Double CAP_JUMP_DIST = 10.0;

    private int speedUnitToggle, timeUnitToggle;
    private double resultMaxSpeed, resultAvgSpeed, resultMaxAir, resultJumpDist;
    private double UCMaxSpeed, UCAvgSpeed, UCMaxAir, UCJumpDist;

    private RoundedImageView ivProfile;
    private TypefaceTextView tvUserName;

    private ProgressBar[] pbars;
    private TypefaceTextView[] tvTrackValues;
    private TypefaceTextView[] tvTrackValueUnits;

    private RippleView doneBtn;
    private TypefaceTextView tvDone;
    private RippleView continueBtn;
    private TypefaceTextView tvContinue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_result, container, false);
        initValues();
        findView(rootView);
        bindEvent();
        return rootView;
    }

    private void initValues() {
        resultMaxSpeed = parentActivity.getMaxSpeed();
        resultAvgSpeed = parentActivity.getAvgSpeed();
        resultMaxAir = parentActivity.getMaxAirTime();
        resultJumpDist = parentActivity.getLongestJump();

        pbars = new ProgressBar[NUMBER_OF_BARS];
        tvTrackValues = new TypefaceTextView[NUMBER_OF_BARS];
        tvTrackValueUnits = new TypefaceTextView[NUMBER_OF_BARS];
    }

    private void findView(View rootView) {
        ivProfile = (RoundedImageView) rootView.findViewById(R.id.iv_profile);
        tvUserName = (TypefaceTextView) rootView.findViewById(R.id.tv_name);

        continueBtn = (RippleView) rootView.findViewById(R.id.result_continue_btn);
        doneBtn = (RippleView) rootView.findViewById(R.id.result_done_btn);
        tvDone = (TypefaceTextView) rootView.findViewById(R.id.tv_done);
        tvContinue = (TypefaceTextView) rootView.findViewById(R.id.tv_continue);

        pbars[0] = (ProgressBar) rootView.findViewById(R.id.bar_max_speed);
        pbars[1] = (ProgressBar) rootView.findViewById(R.id.bar_avg_speed);
        pbars[2] = (ProgressBar) rootView.findViewById(R.id.bar_max_air);
        pbars[3] = (ProgressBar) rootView.findViewById(R.id.bar_jump_distance);

        tvTrackValues[0] = (TypefaceTextView) rootView.findViewById(R.id.tv_max_speed);
        tvTrackValueUnits[0] = (TypefaceTextView) rootView.findViewById(R.id.tv_max_speed_unit);
        tvTrackValues[1] = (TypefaceTextView) rootView.findViewById(R.id.tv_avg_speed);
        tvTrackValueUnits[1] = (TypefaceTextView) rootView.findViewById(R.id.tv_avg_speed_unit);
        tvTrackValues[2] = (TypefaceTextView) rootView.findViewById(R.id.tv_max_air_time);
        tvTrackValueUnits[2] = (TypefaceTextView) rootView.findViewById(R.id.tv_max_air_time_unit);
        tvTrackValues[3] = (TypefaceTextView) rootView.findViewById(R.id.tv_jump_distance);
        tvTrackValueUnits[3] = (TypefaceTextView) rootView.findViewById(R.id.tv_jump_distance_unit);

        for (int index= 0; index<NUMBER_OF_BARS; index++){
            pbars[index].setTag(tvTrackValues[index]);
            tvTrackValues[index].setTag(tvTrackValueUnits[index]);
        }
    }

    private void bindEvent() {
        doneBtn.setOnTouchListener(buttonTouchListener);
        continueBtn.setOnTouchListener(buttonTouchListener);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.stopTracking();
                parentActivity.backFromResultPage();
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.backFromResultPage();
            }
        });

        //this is to prevent hints shown again after onResume
        if (tvDone.getAlpha() != 0) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if(getActivity()==null){return;}
                    if (tvDone !=null){
                        tvDone.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_hint));}
                    if (tvContinue !=null){
                        tvContinue.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_hint));}
                }
            }, 4000);
        } else {
            tvDone.setAlpha(1);
            tvContinue.setAlpha(1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String userName = sp.getString(Constants.SP_USER_NAME, "");
        if (!userName.trim().isEmpty()){
            tvUserName.setText(userName);
        }
        // TODO: Load profile image
//        String photoPath = sp.getString(Constants.SP_PROFILE_PHOTO_PATH, "");
//        if (!photoPath.trim().isEmpty()){
//
//
//        }

        for (ProgressBar pbar : pbars){
            initProgressBar(pbar);
        }

        speedUnitToggle = sp.getInt(Constants.SP_SPEED_UNIT, 0);
        timeUnitToggle = sp.getInt(Constants.SP_TIME_UNIT, 0);

        switch (speedUnitToggle){
            case 1:
                tvTrackValueUnits[0].setText("m/s");
                UCMaxSpeed = 1.0;
                tvTrackValueUnits[1].setText("m/s");
                UCAvgSpeed = 1.0;
                tvTrackValueUnits[3].setText("m");
                UCJumpDist = 1.0;
                break;
            case 2:
                tvTrackValueUnits[0].setText("mi/h");
                UCMaxSpeed = Constants.UC_MS_TO_MIH;
                tvTrackValueUnits[1].setText("mi/h");
                UCAvgSpeed = Constants.UC_MS_TO_MIH;
                tvTrackValueUnits[3].setText("ft");
                UCJumpDist = Constants.UC_M_TO_FT;
                break;
            default:
                tvTrackValueUnits[0].setText("km/h");
                UCMaxSpeed = Constants.UC_MS_TO_KMH;
                tvTrackValueUnits[1].setText("km/h");
                UCAvgSpeed = Constants.UC_MS_TO_KMH;
                tvTrackValueUnits[3].setText("m");
                UCJumpDist = 1.0;
                break;
        }

        switch (timeUnitToggle){
            case 1:
                tvTrackValueUnits[2].setText("ms");
                UCMaxAir = Constants.UC_MILLISECONDS_IN_SECOND;
                break;
            default:
                tvTrackValueUnits[2].setText("sec");
                UCMaxAir = 1.0;
                break;
        }

        startGroupAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        tvDone.setAlpha(0);
        tvContinue.setAlpha(0);
    }

    private void initProgressBar(final ProgressBar pbar){
        pbar.setProgress(0);
        pbar.setMax(100);
    }

    private void startGroupAnimation(){
        continueBtn.postDelayed(new Runnable() {
            @Override
            public void run() {
                startBarAnimations(ANIMATION_STEPS * CAP_MAX_SPEED * UCMaxSpeed,
                        ANIMATION_STEPS * resultMaxSpeed * UCMaxSpeed, pbars[0]);
                startBarAnimations(ANIMATION_STEPS * CAP_AVG_SPEED * UCAvgSpeed,
                        ANIMATION_STEPS * resultAvgSpeed * UCAvgSpeed, pbars[1]);
                startBarAnimations(ANIMATION_STEPS * CAP_AIR_TIME * UCMaxAir,
                        ANIMATION_STEPS * resultMaxAir * UCMaxAir, pbars[2]);
                startBarAnimations(ANIMATION_STEPS * CAP_JUMP_DIST * UCJumpDist,
                        ANIMATION_STEPS * resultJumpDist * UCJumpDist, pbars[3]);
            }
        }, getResources().getInteger(R.integer.fragment_replace_anim_duration));
    }

    private void startBarAnimations(final double capValue, final double endValue, final ProgressBar pbar){
        final TypefaceTextView tv = (TypefaceTextView) pbar.getTag();
        final TypefaceTextView tvu = (TypefaceTextView) tv.getTag();

        pbar.setMax((int) Math.ceil(capValue));

        final double step = capValue/ANIMATION_STEPS;

        new Thread(){
            public void run() {
                double loopValue = 0.0;
                while (loopValue <= endValue) {
                    try {
                        final double progress = loopValue;
                        final float[] hsv = new float[3];
                        hsv[0] = (float) ((1-loopValue/capValue)*220 - 20);
                        if (hsv[0]<0) hsv[0] = 0.0f;
                        hsv[1] = 0.9f;
                        hsv[2] = 0.9f;
                        final int color = Color.HSVToColor(hsv);
                        parentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ((int) progress <= pbar.getMax()) {
                                    pbar.setProgress((int) progress);
                                    LayerDrawable dra = (LayerDrawable) pbar.getProgressDrawable();
                                    dra.findDrawableByLayerId(R.id.progress).setColorFilter(color, PorterDuff.Mode.SRC_IN);
                                    tv.setTextColor(color);
                                    tvu.setTextColor(color);
                                }
                                double tvValue = progress/ANIMATION_STEPS;
                                if (tvValue<10){
                                    tv.setText(sig2.format(progress / ANIMATION_STEPS));
                                }else{
                                    tv.setText(sig3.format(progress/ANIMATION_STEPS));
                                }
                            }
                        });
                        Thread.sleep(ANIMATION_STEP_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    loopValue += step;
                    if (loopValue>endValue && loopValue<(endValue+step*0.9)){
                        loopValue = endValue;
                    }
                }
            }
        }.start();
    }

    private View.OnTouchListener buttonTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = MotionEventCompat.getActionMasked(event);

            switch (action) {
                case (MotionEvent.ACTION_DOWN):
                    ((RippleView) v).animateRipple(event);
                    if (v== doneBtn)
                        tvDone.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_hint));
                    if (v== continueBtn)
                        tvContinue.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_hint));
                    return false;

                case (MotionEvent.ACTION_UP):
                    ((RippleView) v).animateRipple(event);
                    if (v== doneBtn)
                        tvDone.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_hint));
                    if (v== continueBtn)
                        tvContinue.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_hint));
                    return false;
                default:
                    return false;
            }
        }
    };
}
