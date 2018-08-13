package com.batsamayi.ndincedetu;

import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public abstract class InputLayout extends TextInputLayout {
    View root;
    TextView textView;

    public InputLayout(Context context) {
        super(context);
        init(context);
    }

    public InputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public abstract void init(Context context);

    public String getText() {
        return textView.getText().toString().trim();
    }

    public void setText(String string) {
        textView.setText(string);
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        return textView.requestFocus(direction, previouslyFocusedRect);
    }

    public abstract boolean isValid();

    boolean setError(int error) {
        textView.setError(getContext().getString(error));
        requestFocus();
        return false;
    }

    public boolean isValid(String stringToMatch) {
        if(!getText().equals(stringToMatch))
            return setError(R.string.error_match);
        else return isValid();
    }
    public boolean isValid(InputLayout fieldToMatch) {
        return isValid(fieldToMatch.getText());
    }

    public boolean notEmpty() {
        return !TextUtils.isEmpty(getText()) || setError(R.string.error_field_required);
    }
}