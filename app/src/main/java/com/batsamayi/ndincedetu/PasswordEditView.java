package com.batsamayi.ndincedetu;

import android.content.Context;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatEditText;

public class PasswordEditView extends AppCompatEditText {
    public PasswordEditView(Context context) {
        super(context);
    }

    public PasswordEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PasswordEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onSelectionChanged(int start, int end) {
        CharSequence text = getText();
        if (text != null) {
            if (start != text.length() || end != text.length()) {
                setSelection(text.length(), text.length());
                return;
            }
        }

        super.onSelectionChanged(start, end);
    }
}
