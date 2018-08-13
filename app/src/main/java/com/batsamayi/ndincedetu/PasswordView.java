package com.batsamayi.ndincedetu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class PasswordView extends InputLayout {
    public PasswordView(Context context) {
        super(context);
    }

    public PasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Context context) {
        root = inflate(context, R.layout.view_password, this);
        textView = root.findViewById(R.id.PASSWORD_FIELD);
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener onEditorActionListener) {
        textView.setOnEditorActionListener(onEditorActionListener);
    }

    public boolean isValid() {
        return notEmpty() && (getText().length() > 4 || setError(R.string.error_invalid_password));
    }
}

