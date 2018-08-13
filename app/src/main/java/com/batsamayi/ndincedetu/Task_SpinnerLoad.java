package com.batsamayi.ndincedetu;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Task_SpinnerLoad extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<FaultLog> activity;

    private Task_SpinnerLoad(FaultLog activity) {
        this.activity = new WeakReference<>(activity);
    }

    public static void run(FaultLog activity) {
        if (activity.taskSpinnerLoad == null) {
            activity.showProgress(true);
            activity.taskSpinnerLoad = new Task_SpinnerLoad(activity);
            activity.taskSpinnerLoad.execute((Void) null);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        FaultLog fragment = activity.get();
        ServerConnector server = new ServerConnector();
        Dialog_Modal errorDialog = Dialog_Modal.newInstance("Cannot load dropdown menus\nIf this error persists, please contact the admin.");
        //populate spinners with default values
        try {
            server.getFaultCategories();
        } catch (Exception e) {
            errorDialog.show(fragment.getFragmentManager(), "spinnerError");
            return false;
        }
        try {
            server.getFaultItems();
        } catch (Exception e) {
            errorDialog.show(fragment.getFragmentManager(), "spinnerError");
            return false;
        }
        try {
            server.getFaultLocations();
        } catch (Exception e) {
            errorDialog.show(fragment.getFragmentManager(), "spinnerError");
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        FaultLog fragment = activity.get();
        fragment.taskSpinnerLoad = null;
        fragment.showProgress(false);

        Spinner spnFaultCategory = fragment.spnFaultCategory;
        Spinner spnFaultItem = fragment.spnFaultItem;
        Spinner spnFaultLocation = fragment.spnFaultLocation;

        Context context = activity.get().getActivity();

        if (success) {
            fragment.categories = FaultCategory.knownSpinnables;
            setSpinnerAdapter(spnFaultCategory, fragment.categories, context);

            fragment.items = FaultItem.knownSpinnables;
            setSpinnerAdapter(spnFaultItem, fragment.items, context);

            fragment.locations = FaultLocation.knownSpinnables;
            setSpinnerAdapter(spnFaultLocation, fragment.locations, context);
        } else {
            new AlertDialog.Builder(context)
                    .setTitle("Error")
                    .setCancelable(false)
                    .setNegativeButton("OK", null)
                    .setMessage(ServerConnector.ERROR_RESOLVE_HOST)
                    .create().show();
        }
    }


    public static void setSpinnerAdapter(Context context, Spinner spinner, List<String> stringArray) {
        if (stringArray.size() <= 1)
            stringArray = new ArrayList<String>(){ { add("No options available"); } };
        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, stringArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        } catch (Exception ignored) { }
    }

    private static <T> void setSpinnerAdapter(Spinner spinner, SparseArray<T> itemArray, Context context) {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < itemArray.size(); i++) {
            stringList.add(itemArray.valueAt(i).toString());
        }
        Collections.sort(stringList);
        setSpinnerAdapter(context, spinner, stringList);
    }

    @Override
    protected void onCancelled() {
        FaultLog fragment = activity.get();
        fragment.taskSpinnerLoad = null;
        fragment.showProgress(false);
    }
}
