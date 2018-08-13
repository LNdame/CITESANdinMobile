package com.batsamayi.ndincedetu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class EmailAddressView extends InputLayout {
    public EmailAddressView(Context context) {
        super(context);
    }

    public EmailAddressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmailAddressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void init(Context context) {
        root = inflate(context, R.layout.view_email_address, this);
        textView = root.findViewById(R.id.EMAIL_FIELD);
    }

    public boolean isValid() {
        return notEmpty() && (Patterns.EMAIL_ADDRESS.matcher(getText()).matches() || setError(R.string.error_invalid_email));
    }

    public void setAdapter(ArrayAdapter<String> adapter) {
        ((AutoCompleteTextView)textView).setAdapter(adapter);
    }
}

