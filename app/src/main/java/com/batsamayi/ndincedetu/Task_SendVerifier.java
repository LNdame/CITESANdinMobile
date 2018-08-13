package com.batsamayi.ndincedetu;

import android.os.AsyncTask;

class Task_SendVerifier extends AsyncTask<Void, Void, Boolean> {
    private final Users user;
    private Task_SendVerifier(Users user) {
        this.user = user;
    }

    public static void run(Users user) {
        new Task_SendVerifier(user).execute((Void) null);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            new ServerConnector().verifierSend(user);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean success) { }

    @Override
    protected void onCancelled() { }
}
