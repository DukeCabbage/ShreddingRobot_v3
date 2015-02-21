package com.gloomy.shreddingrobot.Utility;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.gloomy.shreddingrobot.MainActivity;

public class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";

    protected MainActivity parentActivity;
    protected SharedPreferences sp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        parentActivity = (MainActivity) getActivity();
        sp = getActivity().getSharedPreferences("ShreddingPref", Context.MODE_PRIVATE);
    }

    protected void hideKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    protected void showKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

}
