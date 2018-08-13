package com.batsamayi.ndincedetu;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import java.lang.ref.WeakReference;

public class Task_FaultGet extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<Recycler_Fragment> currentActivity;
    private SparseArray<Fault> userFaults;
    private final int userID;

    private Task_FaultGet(WeakReference<Recycler_Fragment> currentActivity, int userID) {
        this.currentActivity = currentActivity;
        this.userID = userID;
    }

    public static void run(Recycler_Fragment currentActivity) {
        if (currentActivity.taskGetData == null) {
            currentActivity.swipeRefreshLayout.setRefreshing(true);
            currentActivity.taskGetData = new Task_FaultGet(new WeakReference<>(currentActivity),
                    Cookies.getInstance(currentActivity.getActivity()).userGetCurrent().Id);
            currentActivity.taskGetData.execute((Void) null);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            currentActivity.get().showProgress(true);
            //Prepare data for adaptation
            ServerConnector sc = new ServerConnector();
            userFaults = sc.getAllFaults(userID);
            sc.getFaultItems();
            sc.getFaultCategories();
            sc.getFaultLocations();
            sc.getFaultStatuses();
            return true;
        } catch (Exception e){
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        final Recycler_Fragment caller = currentActivity.get();
        final Activity activity = caller.getActivity();

        RecyclerView rcyFault = caller.rcyData;
        View txtNoFaults = caller.txtNoData;
        finish();

        if (success) {
            int size = userFaults.size();
            if (size > 0 && userFaults.valueAt(0).ID != -1) {
                // specify an adapter
                if (caller.adapter.getItemCount() != size) {
                    if(caller.type == caller.PREVIEW) {
                        rcyFault.setItemViewCacheSize(size);
                        caller.adapter = new Adapter_Fault(userFaults);
                    } else if(caller.type == caller.NOTIFICATION) {
                        SparseArray<Fault> userNotifications = new SparseArray<>(0);
                        for(int i = 0; i < userFaults.size(); i++) {
                            Fault fault = userFaults.valueAt(i);
                            Fault tempFault = copyFault(fault, 1);
                            userNotifications.append(Integer.parseInt(tempFault.Description), tempFault);
                            try {
                                tempFault = copyFault(fault, 2);
                                userNotifications.append(Integer.parseInt(tempFault.Description), tempFault);
                            } catch (Exception ignored) { }
                            try {
                                tempFault = copyFault(fault, 3);
                                userNotifications.append(Integer.parseInt(tempFault.Description), tempFault);
                            } catch (Exception ignored) { }
                        }
                        rcyFault.setItemViewCacheSize(userNotifications.size());
                        caller.adapter = new Adapter_Notification(userNotifications);
                    }
                    rcyFault.setAdapter(caller.adapter);
                    rcyFault.setVisibility(View.VISIBLE);
                    txtNoFaults.setVisibility(View.GONE);
                }
            } else {
                rcyFault.setVisibility(View.GONE);
                txtNoFaults.setVisibility(View.VISIBLE);
                txtNoFaults.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity.getFragmentManager().beginTransaction().replace(R.id.content_frame, new FaultLog()).commit();
                        activity.startActivity(new Intent(activity, Template_Navigation.class));
                    }
                });
            }
        } else {
            Snackbar.make(rcyFault, activity.getString(R.string.label_no_new_faults), Snackbar.LENGTH_LONG)
                    .setAction("Log one", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.getFragmentManager().beginTransaction().replace(R.id.content_frame, new FaultLog()).commit();
                            // activity.startActivity(new Intent(activity, Template_Navigation.class));
                        }
                    }).show();
        }
    }

    private Fault copyFault(Fault original, int status) {
        Fault newFault = new Fault();
        newFault.ID = original.ID;
        newFault.RefNo = original.RefNo;
        String longID = String.format("%04d", original.ID);
        switch (status) {
            case 1:
                newFault.Description = formatDate(original.DateLogged) + longID;
                break;
            case 2:
                newFault.Description = formatDate(original.DateOpened) + longID;
                break;
            case 3:
                newFault.Description = formatDate(original.DateClosed) + longID;
                break;
        }
        newFault.Status = status;
        return newFault;
    }

    private String formatDate(String date) {
        return date.split(" ")[0]
                .replaceFirst("2018", "18")
                .replace("/", "")
                .replace("-", "");
    }

    private void finish() {
        Recycler_Fragment activity = currentActivity.get();
        activity.swipeRefreshLayout.setRefreshing(false);
        activity.taskGetData = null;
        activity.showProgress(false);
    }

    @Override
    protected void onCancelled() {
        finish();
    }
}
