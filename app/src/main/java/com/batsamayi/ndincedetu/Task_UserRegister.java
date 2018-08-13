package com.batsamayi.ndincedetu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Represents an asynchronous registration task used to create
 * the user.
 */
class Task_UserRegister extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<UserRegister> activity;
    private final Users user;
    private String result;

    private Task_UserRegister(UserRegister activity, Users user) {
        this.activity = new WeakReference<>(activity);
        this.user = user;
    }

    public static void run(UserRegister handler, Users user) {
        if (handler.taskUserRegister == null) {
            handler.getValues();
            handler.showProgress(true);
            handler.taskUserRegister = new Task_UserRegister(handler, user);
            handler.taskUserRegister.execute((Void) null);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            ServerConnector cs = new ServerConnector();
            result = cs.userCreate(user);
            if(result != null && !result.toLowerCase().contains("error")) {
                cs.verifierSend(user);
                return true;
            } else
                return false;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        finish();

        final UserRegister caller = activity.get();
        if (success) {
            Cookies cookies = Cookies.getInstance(caller);
            cookies.userLogin(user);
            caller.showModificationForm();
        } else {
            //if user does not exist
            AlertDialog.Builder errorDialog = new AlertDialog.Builder(caller);
            errorDialog.setTitle("ERROR")
                    .setMessage(result);
            if(result.toLowerCase().contains("proceed to login")) {
                errorDialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent startIntent = new Intent(caller, UserLogin.class);
                        startIntent.putExtra(R.string.package_name + ".username", user.Username);
                        startIntent.putExtra(R.string.package_name + ".password", user.Password);
                        caller.startActivity(startIntent);
                    }
                })
                        .setCancelable(true);
            } else {
                errorDialog.setPositiveButton("Forgot Password", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent startIntent = new Intent(caller, ForgotPassword.class);
                            startIntent.putExtra(R.string.package_name + ".username", user.Username);
                            caller.startActivity(startIntent);
                        }
                    })
                    .setNegativeButton("OK", null);
            }
            errorDialog.show();
        }
    }

    @Override
    protected void onCancelled() {
        finish();
    }

    private void finish() {
        UserRegister caller = activity.get();
        caller.taskUserRegister = null;
        caller.showProgress(false);
    }
}
