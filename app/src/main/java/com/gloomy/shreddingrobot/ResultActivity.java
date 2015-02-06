package com.gloomy.shreddingrobot;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class ResultActivity extends ActionBarActivity {
    private static final String TAG = ResultActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary));

        final TextView morph = (TextView) findViewById(R.id.tv_morph);
        morph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onclick");
                animateDrawables(morph);
            }
        });

    }


    private void animateDrawables(View view) {
        if (!(view instanceof TextView)) {
            return;
        }
        TextView textView = (TextView) view;

        if (textView.getBackground() instanceof Animatable) {
            ((Animatable) textView.getBackground()).start();
        }

    }
}
