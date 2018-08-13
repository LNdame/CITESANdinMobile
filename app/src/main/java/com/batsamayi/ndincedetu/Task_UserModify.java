package com.batsamayi.ndincedetu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Represents an asynchronous task used to modify
 * the user's first and last name.
 */
class Task_UserModify extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<UserRegister> activity;
    private final Users user;
    private String result;

    private Task_UserModify(UserRegister activity, Users user) {
        this.activity = new WeakReference<>(activity);
        this.user = user;
    }

    public static void run(UserRegister handler, Users user) {
        if (handler.taskUserModify == null) {
            handler.getValues();
            handler.showProgress(true);
            handler.taskUserModify = new Task_UserModify(handler, user);
            handler.taskUserModify.execute((Void) null);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            ServerConnector cs = new ServerConnector();
            result = cs.userModify(user);
            return result != null && !result.toLowerCase().contains("error");
        } catch (Exception e){
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        finish();

        final UserRegister caller = activity.get();
        if (success) {
            caller.showVerificationForm();
        } else {
            AlertDialog.Builder errorDialog = new AlertDialog.Builder(caller);
            errorDialog.setTitle("ERROR")
                    .setMessage(result)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { }
                    })
                    .show();
        }
    }

    @Override
    protected void onCancelled() {
        finish();
    }

    private void finish() {
        UserRegister caller = activity.get();
        caller.taskUserModify = null;
        caller.showProgress(false);
    }
}
