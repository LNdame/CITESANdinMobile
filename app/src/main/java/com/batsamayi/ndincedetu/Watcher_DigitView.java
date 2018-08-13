package com.batsamayi.ndincedetu;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

class Watcher_DigitView implements TextWatcher {
    private final View previous;
    private final View next;

    private Watcher_DigitView(View previous, View next) {
        this.previous = previous;
        this.next = next;
    }

    public static void register(EditText... editText) {
        int last = editText.length - 1;
        if (last > 0) {
            //for the first, digit, set the previous digit to itself
            editText[0].addTextChangedListener(new Watcher_DigitView(editText[0], editText[1]));
            //for the last digit, set the next digit to itself
            editText[last].addTextChangedListener(new Watcher_DigitView(editText[last - 1], editText[last]));
        }

        for (int i = 1; i < last; i++)
            editText[i].addTextChangedListener(new Watcher_DigitView(editText[i - 1], editText[i + 1]));

        for (EditText current : editText)
            current.setSelectAllOnFocus(true);
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        if(text.length()==1)
            next.requestFocus();
        else if(text.length()==0)
            previous.requestFocus();
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }
}
