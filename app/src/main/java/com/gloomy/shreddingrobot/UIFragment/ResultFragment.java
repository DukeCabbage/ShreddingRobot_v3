package com.gloomy.shreddingrobot.UIFragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.andexert.library.RippleView;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.gloomy.shreddingrobot.R;
import com.gloomy.shreddingrobot.Utility.BaseFragment;
import com.gloomy.shreddingrobot.Widget.Logger;
import com.gloomy.shreddingrobot.Widget.TypefaceTextView;


public class ResultFragment extends BaseFragment {
    private static final String TAG = ResultFragment.class.getSimpleName();

    private TypefaceTextView tv_done;
    private TypefaceTextView tv_continue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onResume() {
        Logger.e(TAG, "onResume()");
        findViewAndBindEvent();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        tv_done.setAlpha(0);
        tv_continue.setAlpha(0);
    }


    private void findViewAndBindEvent() {
        final RippleView result_continue_btn = (RippleView) getView().findViewById(R.id.result_continue_btn);
        final RippleView result_done_btn = (RippleView) getView().findViewById(R.id.result_done_btn);
        tv_done = (TypefaceTextView) getView().findViewById(R.id.tv_done);
        tv_continue = (TypefaceTextView) getView().findViewById(R.id.tv_continue);

        result_done_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);

                switch (action) {
                    case (MotionEvent.ACTION_DOWN):
                        result_done_btn.animateRipple(event);
                        tv_done.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_hint));
                        return false;

                    case (MotionEvent.ACTION_UP):
                        result_done_btn.animateRipple(event);
                        tv_done.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_hint));
                        return false;
                    default:
                        return false;
                }
            }
        });

        result_continue_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);

                switch (action) {
                    case (MotionEvent.ACTION_DOWN):
                        result_continue_btn.animateRipple(event);
                        tv_continue.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_hint));
                        return false;
                    case (MotionEvent.ACTION_UP):
                        result_continue_btn.animateRipple(event);
                        tv_continue.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_hint));
                        return false;
                    default:
                        return false;
                }
            }
        });

        result_continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.transitFromResultToTrack();
            }
        });

        result_done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.transitFromResultToTrack();
            }
        });

        //this is to prevent hints shown again after onResume
        if (tv_done.getAlpha() != 0) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    tv_done.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_hint));
                    tv_continue.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_hint));

                }
            }, 4000);
        } else {
            tv_done.setAlpha(1);
            tv_continue.setAlpha(1);
        }

    }


}
