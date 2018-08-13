package com.batsamayi.ndincedetu;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

class Task_PasswordChange extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<ForgotPassword> activity;
    private final Users user;
    private String result;

    private Task_PasswordChange(WeakReference<ForgotPassword> activity, Users user) {
        this.activity = activity;
        this.user = user;
    }

    public static void run(ForgotPassword activity) {
        if (activity.taskPasswordChange == null) {
            WeakReference<ForgotPassword> weakReference = new WeakReference<>(activity);
            Users user = activity.user;
            activity.taskPasswordChange = new Task_PasswordChange(weakReference, user);
            activity.taskPasswordChange.execute((Void) null);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        activity.get().showProgress(true);
        try {
            result = new ServerConnector().changePassword(user);
            return true;
        } catch (Exception e){
            result = e.getMessage();
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        ForgotPassword handler = activity.get();
        if(success) {
            Intent startIntent = new Intent(handler, UserLogin.class);
            startIntent.putExtra(R.string.package_name + ".username", user.Username);
            handler.startActivity(startIntent);
        } else {
            new AlertDialog.Builder(handler)
                    .setTitle("Error")
                    .setMessage(result)
                    .create().show();
        }
        finish();
    }

    @Override
    protected void onCancelled() {
        finish();
    }

    private void finish() {
        ForgotPassword caller = activity.get();
        caller.taskUserVerify = null;
        caller.showProgress(false);
    }
}
