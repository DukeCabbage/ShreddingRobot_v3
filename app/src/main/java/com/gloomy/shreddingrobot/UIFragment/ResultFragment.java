package com.gloomy.shreddingrobot.UIFragment;

import android.graphics.Color;
import android.graphics.PorterDuff;
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
import com.gloomy.shreddingrobot.Widget.TypefaceTextView;

import java.text.DecimalFormat;

public class ResultFragment extends BaseFragment {
    private static final String TAG = ResultFragment.class.getSimpleName();
    private static final Integer ANIMATION_STEPS = 100;
    private static final DecimalFormat sig3 = new DecimalFormat("@@@");
    private static final DecimalFormat sig2 = new DecimalFormat("@@");

    private static final Double CAP_MAX_SPEED = 35.0;
    private static final Double CAP_AVG_SPEED = 20.0;
    private static final Double CAP_AIR_TIME = 5.0;
    private static final Double CAP_JUMP_DIST = 10.0;

    private int speedUnitToggle, timeUnitToggle;
    private double resultMaxSpeed, resultAvgSpeed, resultMaxAir, resultJumpDist;
    private double UCMaxSpeed, UCAvgSpeed, UCMaxAir, UCJumpDist;

    private TypefaceTextView tvMaxSpeed, tvMaxSpeedUnit, tvMaxAir, tvMaxAirUnit,
            tvAvgSpeed, tvAvgSpeedUnit, tvJumpDist, tvJumpDistUnit;
    private ProgressBar pBarMaxSpeed, pBarAvgSpeed, pBarAirTime, pBarJumpDist;

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
        resultMaxSpeed = 31.6;
        resultAvgSpeed = 12.4;
        resultMaxAir = 2.35;
        resultJumpDist = 8.9;
    }

    private void findView(View rootView) {
        continueBtn = (RippleView) rootView.findViewById(R.id.result_continue_btn);
        doneBtn = (RippleView) rootView.findViewById(R.id.result_done_btn);
        tvDone = (TypefaceTextView) rootView.findViewById(R.id.tv_done);
        tvContinue = (TypefaceTextView) rootView.findViewById(R.id.tv_continue);

        pBarMaxSpeed = (ProgressBar) rootView.findViewById(R.id.bar_max_speed);
        pBarAvgSpeed = (ProgressBar) rootView.findViewById(R.id.bar_avg_speed);
        pBarAirTime = (ProgressBar) rootView.findViewById(R.id.bar_max_air);
        pBarJumpDist = (ProgressBar) rootView.findViewById(R.id.bar_jump_distance);

        tvMaxSpeed = (TypefaceTextView) rootView.findViewById(R.id.tv_max_speed);
        tvMaxSpeedUnit = (TypefaceTextView) rootView.findViewById(R.id.tv_max_speed_unit);
        tvMaxAir = (TypefaceTextView) rootView.findViewById(R.id.tv_max_air_time);
        tvMaxAirUnit = (TypefaceTextView) rootView.findViewById(R.id.tv_max_air_time_unit);
        tvAvgSpeed = (TypefaceTextView) rootView.findViewById(R.id.tv_avg_speed);
        tvAvgSpeedUnit = (TypefaceTextView) rootView.findViewById(R.id.tv_avg_speed_unit);
        tvJumpDist = (TypefaceTextView) rootView.findViewById(R.id.tv_jump_distance);
        tvJumpDistUnit = (TypefaceTextView) rootView.findViewById(R.id.tv_jump_distance_unit);
    }

    private void bindEvent() {
        doneBtn.setOnTouchListener(buttonTouchListener);
        continueBtn.setOnTouchListener(buttonTouchListener);

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
        speedUnitToggle = sp.getInt(Constants.SP_SPEED_UNIT, 0);
        timeUnitToggle = sp.getInt(Constants.SP_TIME_UNIT, 0);

        switch (speedUnitToggle){
            case 1:
                tvMaxSpeedUnit.setText("m/s");
                UCMaxSpeed = 1.0;
                tvAvgSpeedUnit.setText("m/s");
                UCAvgSpeed = 1.0;
                tvJumpDistUnit.setText("m");
                UCJumpDist = 1.0;
                break;
            case 2:
                tvMaxSpeedUnit.setText("mi/h");
                UCMaxSpeed = Constants.UC_MS_TO_MIH;
                tvAvgSpeedUnit.setText("mi/h");
                UCAvgSpeed = Constants.UC_MS_TO_MIH;
                tvJumpDistUnit.setText("ft");
                UCJumpDist = Constants.UC_M_TO_FT;
                break;
            default:
                tvMaxSpeedUnit.setText("km/h");
                UCMaxSpeed = Constants.UC_MS_TO_KMH;
                tvAvgSpeedUnit.setText("km/h");
                UCAvgSpeed = Constants.UC_MS_TO_KMH;
                tvJumpDistUnit.setText("m");
                UCJumpDist = 1.0;
                break;
        }

        switch (timeUnitToggle){
            case 1:
                tvMaxAirUnit.setText("ms");
                UCMaxAir = Constants.UC_MILLISECONDS_IN_SECOND;
                break;
            default:
                tvMaxAirUnit.setText("sec");
                UCMaxAir = 1.0;
                break;
        }

        continueBtn.postDelayed(new Runnable() {
            @Override
            public void run() {
                startBarAnimations(ANIMATION_STEPS * CAP_MAX_SPEED * UCMaxSpeed,
                        ANIMATION_STEPS * resultMaxSpeed * UCMaxSpeed, pBarMaxSpeed, tvMaxSpeed, tvMaxSpeedUnit);
                startBarAnimations(ANIMATION_STEPS * CAP_AVG_SPEED * UCAvgSpeed,
                        ANIMATION_STEPS * resultAvgSpeed * UCAvgSpeed, pBarAvgSpeed, tvAvgSpeed, tvAvgSpeedUnit);
                startBarAnimations(ANIMATION_STEPS * CAP_AIR_TIME * UCMaxAir,
                        ANIMATION_STEPS * resultMaxAir * UCMaxAir, pBarAirTime, tvMaxAir, tvMaxAirUnit);
                startBarAnimations(ANIMATION_STEPS * CAP_JUMP_DIST * UCJumpDist,
                        ANIMATION_STEPS * resultJumpDist * UCJumpDist, pBarJumpDist, tvJumpDist, tvJumpDistUnit);
            }
        }, getResources().getInteger(R.integer.fragment_replace_anim_duration));
    }

    @Override
    public void onPause() {
        super.onPause();
        tvDone.setAlpha(0);
        tvContinue.setAlpha(0);
    }

    private void startBarAnimations(final double capValue, final double endValue,
                                    final ProgressBar pbar, final TypefaceTextView tv, final TypefaceTextView tvu){

        pbar.setMax((int) Math.ceil(capValue));
        pbar.setProgress(0);

        final double step = capValue/ANIMATION_STEPS;
        final int stepTime = 33;

        new Thread(){
            public void run() {
                double loopValue = 0.0;
                while (loopValue < endValue) {
                    try {
                        final double progress = loopValue;
                        final float[] hsv = new float[3];
                        hsv[0] = (float) ((1-loopValue/capValue)*200);
                        hsv[1] = 1.0f;
                        hsv[2] = 1.0f;
                        final int color = Color.HSVToColor(hsv);
                        parentActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ((int) progress <= pbar.getMax()) {
                                    pbar.setProgress((int) progress);
//                                    pbar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
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
                        Thread.sleep(stepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    loopValue += step;
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
