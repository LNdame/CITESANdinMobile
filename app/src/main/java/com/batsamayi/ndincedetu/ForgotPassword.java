package com.batsamayi.ndincedetu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Objects;

public class ForgotPassword extends Handler_Verification {
    private LinearLayout lnrForgotPassword;
    private ProgressBar prgForgotPassword;

    private LinearLayout lnrEmailForm;
    private LinearLayout lnrPasswordForm;
    private EmailAddressView txtUsername;
    private PasswordView txtPassword;
    private PasswordView txtPasswordConfirm;


    void getValues() {
        super.getValues();
        user.Username = txtUsername.getText();
        user.Password = txtPassword.getText();
    }

    private Context context;
    Task_PasswordChange taskPasswordChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setTitle(getString(R.string.action_reset_password));

        Intent intent = getIntent();
        context = getBaseContext();

        user = new Users();
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        txtPasswordConfirm = findViewById(R.id.txtPasswordConfirm);

        lnrForgotPassword = findViewById(R.id.lnrForgotPassword);
        prgForgotPassword = findViewById(R.id.prgForgotPassword);

        lnrEmailForm = findViewById(R.id.lnrEmailForm);
        lnrVerifierForm = findViewById(R.id.lnrVerifierForm);
        lnrPasswordForm = findViewById(R.id.lnrPasswordForm);

        if(intent.hasExtra(R.string.package_name + ".username"))
            txtUsername.setText(Objects.requireNonNull(intent.getExtras()).getString(R.string.package_name + ".username"));

        Button bttResetPassword = findViewById(R.id.bttSubmit);
        bttResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.Username = txtUsername.getText();
                Task_SendVerifier.run(user);
                showVerificationForm();
            }
        });
    }

    @Override
    public void showVerificationForm() {
        lnrEmailForm.setVisibility(View.GONE);
        lnrPasswordForm.setVisibility(View.GONE);
        super.showVerificationForm();
        ((TextView) findViewById(R.id.lblPrompt)).setText(R.string.prompt_enter_below);
    }

    private void showPasswordForm() {
        lnrEmailForm.setVisibility(View.GONE);
        lnrVerifierForm.setVisibility(View.GONE);
        lnrPasswordForm.setVisibility(View.VISIBLE);

        findViewById(R.id.bttChange).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptPasswordChange();
            }
        });
    }

    private void attemptPasswordChange() {
        if (taskPasswordChange != null) {
            return;
        }

        // Reset errors.
        txtUsername.setError(null);
        txtPassword.setError(null);
        txtPasswordConfirm.setError(null);

        if (txtUsername.isValid() && txtPassword.isValid() && txtPasswordConfirm.isValid(txtPassword)) {
            // Store values at the time of the registration attempt.
            getValues();
            // Show a progress spinner, and kick off a background task to
            showProgress(true);
            // perform the user registration attempt.
            Task_PasswordChange.run(this);
        }
    }

    /**
     * Shows the progress UI and hides the forms.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        lnrForgotPassword.setVisibility(show ? View.GONE : View.VISIBLE);
        lnrForgotPassword.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                lnrForgotPassword.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        prgForgotPassword.setVisibility(show ? View.VISIBLE : View.GONE);
        prgForgotPassword.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                prgForgotPassword.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    void onCompleteVerificationTask(String verifyResult) {
        showPasswordForm();
    }

    @Override
    public void onBackPressed() {
        getValues();
        Intent startIntent = new Intent(context, UserLogin.class);
        startIntent.putExtra(R.string.package_name + ".username", txtUsername.getText());
        startActivity(startIntent);
    }
}
