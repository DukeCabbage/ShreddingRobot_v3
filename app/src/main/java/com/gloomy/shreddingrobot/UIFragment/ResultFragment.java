package com.gloomy.shreddingrobot.UIFragment;

import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.Utility.BaseFragment;
import com.gloomy.shreddingrobot.Widget.Logger;
import com.gloomy.shreddingrobot.Widget.TypefaceTextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ResultFragment extends BaseFragment {
    private static final String TAG = ResultFragment.class.getSimpleName();
    private static final int TYPE_MAX_SPEED = 0;
    private static final int TYPE_AVG_SPEED = 1;
    private static final int TYPE_MAX_AIR = 2;
    private static final int TYPE_LONGEST_JUMP = 3;

    private static final int MAX_SPEED_GREEN_THRESHOLD = 30;
    private static final int MAX_SPEED_YELLOW_THRESHOLD = 60;
    private static final int MAX_SPEED_RED_THRESHOLD = 90;

    private static final double MAX_AIR_GREEN_THRESHOLD = 0.8;
    private static final double MAX_AIR_YELLOW_THRESHOLD = 1.5;
    private static final double MAX_AIR_RED_THRESHOLD = 2.0;

    private static final int AVG_SPEED_GREEN_THRESHOLD = 20;
    private static final int AVG_SPEED_YELLOW_THRESHOLD = 40;
    private static final int AVG_SPEED_RED_THRESHOLD = 60;

    private static final int JUMP_GREEN_THRESHOLD = 3;
    private static final int JUMP_YELLOW_THRESHOLD = 6;
    private static final int JUMP_RED_THRESHOLD = 9;


    private static final int COLOR_TRANSITION_TIME = 200;

    private RippleView done_btn;
    private TypefaceTextView tv_done;
    private RippleView continue_btn;
    private TypefaceTextView tv_continue;
    private View bar_max_air, bar_max_speed, bar_longest_jump, bar_avg_speed;
    private TextView tv_max_speed,tv_max_speed_unit, tv_max_air, tv_max_air_unit, tv_avg_speed, tv_avg_speed_unit, tv_longest_jump, tv_longest_jump_unit;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onResume() {
        findViewAndBindEvent(getView());

        continue_btn.post(new Runnable() {
            @Override
            public void run() {
                //TODO:calculate width of bar dynamacally
                int width = 450;
                int max_speed = 120;
                double max_air = 1.2;
                int avg_speed = 30;
                int best_jump = 7;

                ResizeWidthAnimation anim4 = new ResizeWidthAnimation(bar_max_air, tv_max_air, 350, 0 , max_air, TYPE_MAX_AIR);
                anim4.setDuration(3000);
                anim4.setInterpolator(new DecelerateInterpolator());
                anim4.setStartOffset(200);
                bar_max_air.startAnimation(anim4);

                ResizeWidthAnimation anim3 = new ResizeWidthAnimation(bar_longest_jump, tv_longest_jump, 250, best_jump, 0.0, TYPE_LONGEST_JUMP);
                anim3.setDuration(3000);
                anim3.setInterpolator(new DecelerateInterpolator());
                anim3.setStartOffset(400);
                bar_longest_jump.startAnimation(anim3);

                ResizeWidthAnimation anim = new ResizeWidthAnimation(bar_max_speed, tv_max_speed, 400, max_speed, 0.0, TYPE_MAX_SPEED);
                anim.setDuration(3000);
                anim.setInterpolator(new DecelerateInterpolator());
                anim4.setStartOffset(600);
                bar_max_speed.startAnimation(anim);

                ResizeWidthAnimation anim2 = new ResizeWidthAnimation(bar_avg_speed, tv_avg_speed, 150, avg_speed, 0.0, TYPE_AVG_SPEED);
                anim2.setDuration(3000);
                anim2.setInterpolator(new DecelerateInterpolator());
                anim2.setStartOffset(800);
                bar_avg_speed.startAnimation(anim2);
            }
        });
//        initAnimator();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        tv_done.setAlpha(0);
        tv_continue.setAlpha(0);
    }

    private void findViewAndBindEvent(View rootView) {
        continue_btn = (RippleView) rootView.findViewById(R.id.result_continue_btn);
        done_btn = (RippleView) rootView.findViewById(R.id.result_done_btn);
        tv_done = (TypefaceTextView) rootView.findViewById(R.id.tv_done);
        tv_continue = (TypefaceTextView) rootView.findViewById(R.id.tv_continue);


        bar_max_air =  rootView.findViewById(R.id.bar_max_air);
        bar_max_speed =  rootView.findViewById(R.id.bar_max_speed);
        bar_longest_jump = rootView.findViewById(R.id.bar_longest_jump);
        bar_avg_speed = rootView.findViewById(R.id.bar_avg_speed);

        tv_max_speed = (TextView) rootView.findViewById(R.id.tv_max_speed);
        tv_max_speed_unit = (TextView) rootView.findViewById(R.id.tv_max_speed_unit);
        tv_max_air = (TextView) rootView.findViewById(R.id.tv_max_air);
        tv_max_air_unit = (TextView) rootView.findViewById(R.id.tv_max_air_unit);
        tv_avg_speed = (TextView) rootView.findViewById(R.id.tv_avg_speed);
        tv_avg_speed_unit = (TextView) rootView.findViewById(R.id.tv_avg_speed_unit);
        tv_longest_jump = (TextView) rootView.findViewById(R.id.tv_longest_jump);
        tv_longest_jump_unit = (TextView) rootView.findViewById(R.id.tv_longest_jump_unit);

        done_btn.setOnTouchListener(buttonTouchListener);
        continue_btn.setOnTouchListener(buttonTouchListener);

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //parentActivity.backFromResultPage();
                bar_longest_jump.setBackgroundResource(R.drawable.transition_yellow_red);
                TransitionDrawable transition = (TransitionDrawable) bar_longest_jump.getBackground();
                transition.startTransition(500);
            }
        });

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //parentActivity.backFromResultPage();


            }
        });

        //this is to prevent hints shown again after onResume
        if (tv_done.getAlpha() != 0) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if(getActivity()==null){return;}
                    if (tv_done!=null){tv_done.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_hint));}
                    if (tv_continue!=null){tv_continue.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_hint));}
                }
            }, 4000);
        } else {
            tv_done.setAlpha(1);
            tv_continue.setAlpha(1);
        }
    }

    private View.OnTouchListener buttonTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = MotionEventCompat.getActionMasked(event);

            switch (action) {
                case (MotionEvent.ACTION_DOWN):
                    ((RippleView) v).animateRipple(event);
                    if (v==done_btn)
                        tv_done.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_hint));
                    if (v==continue_btn)
                        tv_continue.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_hint));
                    return false;

                case (MotionEvent.ACTION_UP):
                    ((RippleView) v).animateRipple(event);
                    if (v==done_btn)
                        tv_done.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_hint));
                    if (v==continue_btn)
                        tv_continue.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_hint));
                    return false;
                default:
                    return false;
            }
        }
    };





    private class ResizeWidthAnimation extends Animation
    {
        private int mWidth;
        private int mStartWidth;
        private View mView;
        private int endValue;
        private double doubleEndValue;
        private TextView textView;
        private int type;
        private Boolean green_reach = false;
        private Boolean yellow_reach = false;
        private Boolean red_reach = false;


        public ResizeWidthAnimation(View view, TextView textView, int width, int endValue, double doubleEndValue, int type)
        {
            mView = view;
            mWidth = width;
            mStartWidth = view.getWidth();
            this.endValue = endValue;
            this.textView = textView;
            this.type = type;
            this.doubleEndValue = doubleEndValue;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t)
        {
            if(type == TYPE_MAX_AIR) {
                int newWidth = mStartWidth + (int) ((mWidth - mStartWidth) * interpolatedTime);
                double newValue = (double) (doubleEndValue * interpolatedTime);
                DecimalFormat df = new DecimalFormat("#.#");
                String dx=df.format(newValue);
                newValue=Double.valueOf(dx);
                textView.setText(newValue+"");
                mView.getLayoutParams().width = newWidth;
                mView.requestLayout();
                animateColor(TYPE_MAX_AIR, 0, newValue);
            }
            else {
                int newWidth = mStartWidth + (int) ((mWidth - mStartWidth) * interpolatedTime);
                int newValue = (int) (endValue * interpolatedTime);
                textView.setText(newValue+"");
                mView.getLayoutParams().width = newWidth;
                mView.requestLayout();
                animateColor(type, newValue, 0.0);
            }
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight)
        {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds()
        {
            return true;
        }

        private void animateColor(int type, int newValue, double doubleNewValue){
            if(type == TYPE_MAX_AIR){
                if(doubleNewValue>MAX_AIR_GREEN_THRESHOLD && !green_reach){
                    Logger.e(TAG,"reach MAX_SPEED_GREEN_THRESHOLD");
                    bar_max_air.setBackgroundResource(R.drawable.transition_white_green);
                    TransitionDrawable transition = (TransitionDrawable) bar_max_air.getBackground();
                    transition.startTransition(COLOR_TRANSITION_TIME);
                    tv_max_air.setTextColor(getResources().getColor(R.color.green));
                    tv_max_air_unit.setTextColor(getResources().getColor(R.color.green));
                    green_reach=true;
                }
                if(doubleNewValue>MAX_AIR_YELLOW_THRESHOLD && !yellow_reach){
                    bar_max_air.setBackgroundResource(R.drawable.transition_green_yellow);
                    TransitionDrawable transition = (TransitionDrawable) bar_max_air.getBackground();
                    transition.startTransition(COLOR_TRANSITION_TIME);
                    tv_max_air.setTextColor(getResources().getColor(R.color.lime));
                    tv_max_air_unit.setTextColor(getResources().getColor(R.color.lime));
                    yellow_reach = true;
                }

                if(doubleNewValue>MAX_AIR_RED_THRESHOLD  && !red_reach){
                    bar_max_air.setBackgroundResource(R.drawable.transition_yellow_red);
                    TransitionDrawable transition = (TransitionDrawable) bar_max_air.getBackground();
                    transition.startTransition(COLOR_TRANSITION_TIME);
                    tv_max_air.setTextColor(getResources().getColor(R.color.red));
                    tv_max_air_unit.setTextColor(getResources().getColor(R.color.red));
                    red_reach=true;
                }
            }
            else {
                View bar = null;
                TextView textView = null;
                TextView unit = null;
                int threadHold1;
                int threadHold2;
                int threadHold3;
                switch (type) {
                    case TYPE_AVG_SPEED:
                        bar = bar_avg_speed;
                        textView = tv_avg_speed;
                        unit = tv_avg_speed_unit;
                        threadHold1 = AVG_SPEED_GREEN_THRESHOLD;
                        threadHold2 = AVG_SPEED_YELLOW_THRESHOLD;
                        threadHold3 = AVG_SPEED_RED_THRESHOLD;
                        break;
                    case TYPE_LONGEST_JUMP:
                        bar = bar_longest_jump;
                        textView = tv_longest_jump;
                        unit = tv_longest_jump_unit;
                        threadHold1 = JUMP_GREEN_THRESHOLD;
                        threadHold2 = JUMP_YELLOW_THRESHOLD;
                        threadHold3 = JUMP_RED_THRESHOLD;
                        break;
                    case TYPE_MAX_SPEED:
                        bar = bar_max_speed;
                        textView = tv_max_speed;
                        unit = tv_max_speed_unit;
                        threadHold1 = MAX_SPEED_GREEN_THRESHOLD;
                        threadHold2 = MAX_SPEED_YELLOW_THRESHOLD;
                        threadHold3 = MAX_SPEED_RED_THRESHOLD;
                        break;
                    default:
                        threadHold1 = 0;
                        threadHold2 = 0;
                        threadHold3 = 0;
                }

                if (newValue > threadHold1 && !green_reach) {
                    bar.setBackgroundResource(R.drawable.transition_white_green);
                    TransitionDrawable transition = (TransitionDrawable) bar.getBackground();
                    transition.startTransition(COLOR_TRANSITION_TIME);
                    textView.setTextColor(getResources().getColor(R.color.green));
                    unit.setTextColor(getResources().getColor(R.color.green));
                    green_reach = true;
                }
                if (newValue > threadHold2 && !yellow_reach) {
                    bar.setBackgroundResource(R.drawable.transition_green_yellow);
                    TransitionDrawable transition = (TransitionDrawable) bar.getBackground();
                    transition.startTransition(COLOR_TRANSITION_TIME);
                    textView.setTextColor(getResources().getColor(R.color.lime));
                    unit.setTextColor(getResources().getColor(R.color.lime));
                    yellow_reach = true;
                }

                if (newValue > threadHold3 && !red_reach) {
                    bar.setBackgroundResource(R.drawable.transition_yellow_red);
                    TransitionDrawable transition = (TransitionDrawable) bar.getBackground();
                    transition.startTransition(COLOR_TRANSITION_TIME);
                    textView.setTextColor(getResources().getColor(R.color.red));
                    unit.setTextColor(getResources().getColor(R.color.red));
                    red_reach = true;
                }
            }

        }
    }

}
