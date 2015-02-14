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

    private RippleView done_btn;
    private TypefaceTextView tv_done;
    private RippleView continue_btn;
    private TypefaceTextView tv_continue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onResume() {
        findViewAndBindEvent(getView());
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

        done_btn.setOnTouchListener(buttonTouchListener);
        continue_btn.setOnTouchListener(buttonTouchListener);

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.backFromResultPage();
            }
        });

        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parentActivity.backFromResultPage();
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

}
