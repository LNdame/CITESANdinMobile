package com.batsamayi.ndincedetu;

import android.app.DialogFragment;
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

public class Task_FaultLog extends AsyncTask<Void, Void, Boolean> {
    private String result;

    private final StorageReference mStorageRef;
    private final File pictureDir;
    private final Fault fault;

    private final WeakReference<FaultLog> currentActivity;

    private Task_FaultLog(FaultLog currentActivity) {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        this.currentActivity = new WeakReference<>(currentActivity);
        this.fault = currentActivity.fault;
        this.pictureDir = currentActivity.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    public static void run(FaultLog currentActivity) {
        if (currentActivity.taskFaultLog == null) {
            currentActivity.showProgress(true);
            currentActivity.taskFaultLog = new Task_FaultLog(currentActivity);
            currentActivity.taskFaultLog.execute((Void) null);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        currentActivity.get().showProgress(true);

        try {
            ServerConnector sc = new ServerConnector();
            result = sc.logFault(fault, fault.Picture);

            int faultID = -1;
            for(int loggedFault:sc.getFaultIDs(fault.User))
                if(loggedFault > faultID)
                    faultID = loggedFault;
            File storageDir = new File(pictureDir, "Faults");
            if(!storageDir.exists() && !storageDir.mkdirs()) {
                return null;
            }

            String filename = File.separator + "Fault" + faultID + ".jpg";

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpg")
                    .build();
            StorageTask uploadImageTask = mStorageRef.child("Faults" + filename)
                    .putBytes(fault.Picture, metadata);

            try (FileOutputStream fos = new FileOutputStream(storageDir.getPath() + filename)) {
                fos.write(fault.Picture);
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
        FaultLog activity = currentActivity.get();
        finish();

        if(result.equals("Fault logged")) {
            Toast.makeText(activity.getActivity(), "Fault logged successfully", Toast.LENGTH_LONG).show();
            activity.gpsTracker.stopUsingGPS();
            success = true;
        } else {
            success = false;
        }

        if (success) {
            activity.getFragmentManager().beginTransaction().replace(R.id.content_frame, new FaultsAll()).commit();
        } else {
            DialogFragment errorDialog = Dialog_Modal.newInstance(result);
            errorDialog.show(activity.getFragmentManager(), "incorrectDialog");
        }
    }

    @Override
    protected void onCancelled() {
        finish();
    }

    private void finish() {
        FaultLog activity = currentActivity.get();
        activity.taskFaultLog = null;
        activity.showProgress(false);
    }
}
