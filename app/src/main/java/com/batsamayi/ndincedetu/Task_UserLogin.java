package com.batsamayi.ndincedetu;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Represents an asynchronous login task used to authenticate
 * the user.
 */
class Task_UserLogin extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<UserLogin> activity;
    private Users user;

    private Task_UserLogin(UserLogin activity, Users user) {
        this.activity = new WeakReference<>(activity);
        this.user = user;
    }

    public static void run(UserLogin activity, Users user) {
        if (activity.taskUserLogin == null) {
            activity.getValues();
            activity.showProgress(true);
            activity.taskUserLogin = new Task_UserLogin(activity, user);
            activity.taskUserLogin.execute((Void) null);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            user = new ServerConnector().userLogin(user);
            if (user.Id > -1) {
                Cookies prefs = Cookies.getInstance(activity.get());
                prefs.userLogin(user);
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        finish();

        UserLogin caller = activity.get();
        if (success) {
            caller.finish();
            caller.startActivity(new Intent(caller, Template_Navigation.class));
        } else {
            //if server cannot be reached
            String error = ServerConnector.parseError(user.Username);
            if (error != null) {
                new AlertDialog.Builder(caller).setTitle("ERROR").setMessage(error).create().show();
            } else {
                //if user does not exist
                PasswordView txtPassword = caller.findViewById(R.id.txtPassword);
                Dialog_Modal.newInstance(Dialog_Modal.INCORRECT_PASSWORD, txtPassword.textView)
                        .show(caller.getFragmentManager(), "incorrectDialog");
            }
        }
    }

    @Override
    protected void onCancelled() {
        finish();
    }

    private void finish() {
        UserLogin caller = activity.get();
        caller.taskUserLogin = null;
        caller.showProgress(false);
    }
}
