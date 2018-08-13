package com.batsamayi.ndincedetu;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

abstract class Handler_Verification extends AppCompatActivity {
    void getValues() {
        if(txtVerifyDigit0 == null) {
            txtVerifyDigit0 = findViewById(R.id.txtVerifyDigit0);
            txtVerifyDigit1 = findViewById(R.id.txtVerifyDigit1);
            txtVerifyDigit2 = findViewById(R.id.txtVerifyDigit2);
            txtVerifyDigit3 = findViewById(R.id.txtVerifyDigit3);
        }

        verifier = txtVerifyDigit0.getText().toString()
                + txtVerifyDigit1.getText().toString()
                + txtVerifyDigit2.getText().toString()
                + txtVerifyDigit3.getText().toString();
    }

    abstract void showProgress(boolean show);

    String verifier;
    EditText txtVerifyDigit0;
    EditText txtVerifyDigit1;
    EditText txtVerifyDigit2;
    EditText txtVerifyDigit3;
    Task_UserVerify taskUserVerify;

    LinearLayout lnrVerifierForm;
    Users user;
    void showVerificationForm() {
        lnrVerifierForm = findViewById(R.id.lnrVerifierForm);
        lnrVerifierForm.setVisibility(View.VISIBLE);

        if(txtVerifyDigit0 == null) {
            txtVerifyDigit0 = findViewById(R.id.txtVerifyDigit0);
            txtVerifyDigit1 = findViewById(R.id.txtVerifyDigit1);
            txtVerifyDigit2 = findViewById(R.id.txtVerifyDigit2);
            txtVerifyDigit3 = findViewById(R.id.txtVerifyDigit3);
        }

        Watcher_DigitView.register(txtVerifyDigit0, txtVerifyDigit1, txtVerifyDigit2, txtVerifyDigit3);
        txtVerifyDigit0.requestFocus();

        findViewById(R.id.bttVerify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task_UserVerify.run(Handler_Verification.this);
            }
        });

        txtResend = findViewById(R.id.txtResend);
        txtResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task_SendVerifier.run(user);
                allowCodeResend();
            }
        });
        allowCodeResend();
    }

    private TextView txtResend;
    private void allowCodeResend() {
        final String DEFAULT_TEXT = txtResend.getText().toString();
        txtResend.setEnabled(false);
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String withTimerText = DEFAULT_TEXT + " (" + millisUntilFinished/1000 + ")";
                txtResend.setText(withTimerText);
            }

            @Override
            public void onFinish() {
                txtResend.setText(DEFAULT_TEXT);
                txtResend.setEnabled(true);
            }
        }.start();
    }

    abstract void onCompleteVerificationTask(String verifyResult);
}
