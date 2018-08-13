package com.batsamayi.ndincedetu;

import android.content.Context;
import android.util.AttributeSet;

public class PasswordConfirmView extends PasswordView {

    public PasswordConfirmView(Context context) {
        super(context);
    }

    public PasswordConfirmView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PasswordConfirmView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init(Context context) {
        root = inflate(context, R.layout.view_password_confirm, this);
        textView = root.findViewById(R.id.PASSWORD_FIELD);
    }
}
