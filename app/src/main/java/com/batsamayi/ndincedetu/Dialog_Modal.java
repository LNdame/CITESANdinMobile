package com.batsamayi.ndincedetu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

public class Dialog_Modal extends DialogFragment {
    private static final int CUSTOM = -1;
    public static final int INCORRECT_PASSWORD = 0;
    public static final int INCORRECT_VERIFIER = 1;

    private static TextView[] mTextView;
    public static Dialog_Modal newInstance(int dialogType, TextView... textView) {
        Dialog_Modal dialog = new Dialog_Modal();

        Bundle args = new Bundle();
        args.putInt("dialogType", dialogType);
        dialog.setArguments(args);

        mTextView = textView;
        return dialog;
    }

    private static String customMessage;
    public static Dialog_Modal newInstance(String content) {
        Dialog_Modal dialog = new Dialog_Modal();

        Bundle args = new Bundle();
        args.putInt("dialogType", CUSTOM);
        dialog.setArguments(args);

        customMessage = content;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        switch (getArguments().getInt("dialogType")) {
            case CUSTOM:
                adb.setMessage(customMessage)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { }
                        });
                break;
            case INCORRECT_PASSWORD:
                adb.setMessage(R.string.error_incorrect_password)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (TextView textView:mTextView) {
                                    textView.setText("");
                                }
                                mTextView[0].requestFocus();
                            }
                        });
                break;
            case INCORRECT_VERIFIER:
                adb.setMessage(R.string.error_incorrect_verifier)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (TextView textView:mTextView) {
                                    textView.setText("");
                                }
                                mTextView[0].requestFocus();
                            }
                        });
                break;
        }
        return adb
                .setCancelable(false)
                .create();
    }
}
