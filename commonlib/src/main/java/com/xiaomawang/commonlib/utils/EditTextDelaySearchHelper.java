package com.xiaomawang.commonlib.utils;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class EditTextDelaySearchHelper {


    public static void init(final EditText editText, final OnDelaySearchListener onDelaySearchListener){
        final Handler handler = new Handler();
        final SearchRunnable searchRunnable = new SearchRunnable(onDelaySearchListener);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
                handler.removeCallbacks(searchRunnable);
                searchRunnable.setKeyword(s.toString());
                handler.postDelayed(searchRunnable, 150);
            }
        });
    }

    private static class SearchRunnable implements Runnable {

        OnDelaySearchListener onDelaySearchListener;
        String keyword;

        public SearchRunnable(OnDelaySearchListener onDelaySearchListener) {
            this.onDelaySearchListener = onDelaySearchListener;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public void run() {
            onDelaySearchListener.onDelaySearch(keyword);
        }
    }

    public interface OnDelaySearchListener{
        void onDelaySearch(String keyword);
    }
}
