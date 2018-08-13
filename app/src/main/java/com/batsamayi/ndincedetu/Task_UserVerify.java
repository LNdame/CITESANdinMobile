package com.batsamayi.ndincedetu;

import android.app.DialogFragment;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

class Task_UserVerify extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<Handler_Verification> activity;
    private final Users user;

    private Task_UserVerify(Handler_Verification activity, Users user) {
        this.activity = new WeakReference<>(activity);
        this.user = user;
    }

    public static void run(Handler_Verification handler) {
        handler.getValues();
        Task_UserVerify taskUserVerify = handler.taskUserVerify;
        if (taskUserVerify != null) {
            return;
        }

        TextView txtVerifyDigit0 = handler.txtVerifyDigit0;
        TextView txtVerifyDigit1 = handler.txtVerifyDigit1;
        TextView txtVerifyDigit2 = handler.txtVerifyDigit2;
        TextView txtVerifyDigit3 = handler.txtVerifyDigit3;

        // Reset errors.
        txtVerifyDigit0.setError(null);
        txtVerifyDigit1.setError(null);
        txtVerifyDigit2.setError(null);
        txtVerifyDigit3.setError(null);

        boolean cancel = false;
        View focusView = null;

        String error = handler.getString(R.string.error_field_required);
        // Check that name and surname have been entered
        if (TextUtils.isEmpty(handler.verifier)) {
            if (TextUtils.isEmpty(txtVerifyDigit0.getText())) {
                txtVerifyDigit0.setError(error);
                focusView = txtVerifyDigit0;
                cancel = true;
            } else if (TextUtils.isEmpty(txtVerifyDigit1.getText())) {
                txtVerifyDigit1.setError(error);
                focusView = txtVerifyDigit1;
                cancel = true;
            } else if (TextUtils.isEmpty(txtVerifyDigit2.getText())) {
                txtVerifyDigit2.setError(error);
                focusView = txtVerifyDigit2;
                cancel = true;
            } else if (TextUtils.isEmpty(txtVerifyDigit3.getText())) {
                txtVerifyDigit3.setError(error);
                focusView = txtVerifyDigit3;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt registration and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user registration attempt.
            Users user = handler.user;
            if(user == null)
                user = Cookies.getInstance(handler).userGetCurrent();
            taskUserVerify = new Task_UserVerify(handler, user);
            handler.showProgress(true);
            taskUserVerify.execute((Void) null);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Handler_Verification handler = activity.get();
            user.Verifier = handler.verifier;
            user.IsApproved = true;
            verifyResult = new ServerConnector().userVerify(user);
            if(verifyResult != null && !verifyResult.toLowerCase().contains("error")) {
                Cookies.getInstance(handler).userVerify(user);
                return true;
            } else return false;
        } catch (Exception e){
            return false;
        }
    }

    private String verifyResult = "";
    @Override
    protected void onPostExecute(final Boolean success) {
        finish();
        Handler_Verification handler = activity.get();
        if (success) {
            handler.onCompleteVerificationTask(verifyResult);
        } else {
            DialogFragment dlgIncorrectVerifier = Dialog_Modal.newInstance(Dialog_Modal.INCORRECT_VERIFIER,
                    handler.txtVerifyDigit0, handler.txtVerifyDigit1, handler.txtVerifyDigit2, handler.txtVerifyDigit3);
            dlgIncorrectVerifier.show(handler.getFragmentManager(), "incorrectVerifier");
        }
    }

    @Override
    protected void onCancelled() {
        finish();
    }

    private void finish() {
        Handler_Verification caller = activity.get();
        caller.taskUserVerify = null;
        caller.showProgress(false);
    }
}
