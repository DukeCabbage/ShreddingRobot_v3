package com.gloomy.shreddingrobot.UIFragment;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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

public class ResultFragment extends BaseFragment {
    private static final String TAG = ResultFragment.class.getSimpleName();
    private static final int TYPE_MAX_SPEED = 0;
    private static final int TYPE_AVG_SPEED = 1;
    private static final int TYPE_MAX_AIR = 2;
    private static final int TYPE_LONGEST_JUMP = 3;
    private static final int MAX_SPEED_YELLOW_THRESHOLD = 40;
    private static final int MAX_SPEED_RED_THRESHOLD = 80;
    private static final int COLOR_TRANSITION_TIME = 500;

    private RippleView done_btn;
    private TypefaceTextView tv_done;
    private RippleView continue_btn;
    private TypefaceTextView tv_continue;
    private View bar_max_air, max_speed_bar, longest_jump_bar;
    private TextView tv_max_speed,tv_max_speed_unit;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onResume() {
        findViewAndBindEvent(getView());
//        initAnimator();
        super.onResume();
    }

//    private void initAnimator() {
//        Integer colorFrom = getResources().getColor(R.color.red);
//        Integer colorTo = getResources().getColor(R.color.blue);
//        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
//        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//
//            @Override
//            public void onAnimationUpdate(ValueAnimator animator) {
//                textView.setBackgroundColor((Integer)animator.getAnimatedValue());
//            }
//
//        });
//        colorAnimation.start();
//    }

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

        bar_max_air = rootView.findViewById(R.id.bar_max_air);
        max_speed_bar = rootView.findViewById(R.id.bar_max_speed);
        longest_jump_bar = rootView.findViewById(R.id.bar_jump_distance);
        tv_max_speed = (TextView) rootView.findViewById(R.id.tv_max_speed);
        tv_max_speed_unit = (TextView) rootView.findViewById(R.id.tv_max_speed_unit);

        done_btn.setOnTouchListener(buttonTouchListener);
        continue_btn.setOnTouchListener(buttonTouchListener);

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //parentActivity.backFromResultPage();
                int from = getActivity().getResources().getColor(R.color.green);
                int to = getActivity().getResources().getColor(R.color.lime);
                ObjectAnimator.ofInt(longest_jump_bar, "backgroundColor", from, to)
                        .setDuration(COLOR_TRANSITION_TIME)
                        .start();
            }
        });

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //parentActivity.backFromResultPage();

                ResizeWidthAnimation anim = new ResizeWidthAnimation(max_speed_bar, tv_max_speed, 897, 120, TYPE_MAX_SPEED);
                anim.setDuration(3000);
                anim.setInterpolator(new DecelerateInterpolator());
                max_speed_bar.startAnimation(anim);
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

    private void startAnimation(){
        RelativeLayout.LayoutParams parms = new RelativeLayout.LayoutParams(10,10);
        bar_max_air.setLayoutParams(parms);
    }


    private class ResizeWidthAnimation extends Animation
    {
        private int mWidth;
        private int mStartWidth;
        private View mView;
        private int endValue;
        private TextView textView;
        private int type;



        public ResizeWidthAnimation(View view, TextView textView, int width, int endValue, int type)
        {
            mView = view;
            mWidth = width;
            mStartWidth = view.getWidth();
            this.endValue = endValue;
            this.textView = textView;
            this.type = type;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t)
        {

            int newWidth = mStartWidth + (int) ((mWidth - mStartWidth) * interpolatedTime);
            int newValue = (int) (endValue * interpolatedTime);
            textView.setText(newValue+"");
            mView.getLayoutParams().width = newWidth;
            mView.requestLayout();

            if(type == TYPE_MAX_SPEED){
                if(newValue==MAX_SPEED_YELLOW_THRESHOLD){
                    int from = getActivity().getResources().getColor(R.color.green);
                    int to = getActivity().getResources().getColor(R.color.lime);
                    ObjectAnimator.ofInt(textView, "textColor", from, to)
                            .setDuration(COLOR_TRANSITION_TIME)
                            .start();
                    ObjectAnimator.ofInt(tv_max_speed_unit, "textColor", from, to)
                            .setDuration(COLOR_TRANSITION_TIME)
                            .start();
                    ObjectAnimator.ofInt(mView, "backgroundColor", from, to)
                            .setDuration(COLOR_TRANSITION_TIME)
                            .start();

                }

                if(newValue==MAX_SPEED_RED_THRESHOLD){
                    int from = getActivity().getResources().getColor(R.color.lime);
                    int to = getActivity().getResources().getColor(R.color.red);
                    ObjectAnimator.ofInt(textView, "textColor", getActivity().getResources().getColor(R.color.lime), getActivity().getResources().getColor(R.color.red))
                            .setDuration(COLOR_TRANSITION_TIME)
                            .start();
                    ObjectAnimator.ofInt(tv_max_speed_unit, "textColor", from, to)
                            .setDuration(COLOR_TRANSITION_TIME)
                            .start();
                    ObjectAnimator.ofInt(mView, "backgroundColor", from, to)
                            .setDuration(COLOR_TRANSITION_TIME)
                            .start();
                }
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
    }

}
