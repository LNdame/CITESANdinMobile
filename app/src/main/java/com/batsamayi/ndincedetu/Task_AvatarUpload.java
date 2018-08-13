package com.batsamayi.ndincedetu;

import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;

public class Task_AvatarUpload extends AsyncTask<Void, Void, Boolean> {
    private String result;

    private final StorageReference mStorageRef;
    private final Users user;
    private final WeakReference<UserProfile> currentActivity;

    private Task_AvatarUpload(UserProfile currentActivity) {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        this.currentActivity = new WeakReference<>(currentActivity);
        user = currentActivity.user;
    }

    public static void run(UserProfile currentActivity) {
        if (currentActivity.taskAvatarUpload == null) {
            currentActivity.showProgress(true);
            currentActivity.taskAvatarUpload = new Task_AvatarUpload(currentActivity);
            currentActivity.taskAvatarUpload.execute((Void) null);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        UserProfile caller = currentActivity.get();

        try {
            user.Picture = caller.pictureCompressed;
            String filename = File.separator + "User" + user.Id + ".jpg";

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .build();
            StorageTask uploadImageTask = mStorageRef.child(caller.directory + filename)
                    .putBytes(user.Picture, metadata);

            result = new ServerConnector().uploadAvatar(user);

            File storageDir = new File(caller.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), caller.directory);
            if(!storageDir.exists() && !storageDir.mkdirs())
                return null;

            try(FileOutputStream fos = new FileOutputStream(storageDir.getPath() + filename)) {
                fos.write(user.Picture);
            }

            while(true)
                if(uploadImageTask.isComplete())
                    return uploadImageTask.isSuccessful();

        } catch (Exception e){
            result = e.getMessage();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        UserProfile activity = currentActivity.get();
        finish();

        if(!result.toLowerCase().contains("error") || result.isEmpty())
            Toast.makeText(activity.getActivity(), "Avatar uploaded successfully", Toast.LENGTH_LONG).show();
        else
            Dialog_Modal.newInstance(result).show(activity.getFragmentManager(), "errorDialog");
    }

    @Override
    protected void onCancelled() {
        finish();
    }

    private void finish() {
        UserProfile activity = currentActivity.get();
        activity.taskAvatarUpload = null;
    }
}
