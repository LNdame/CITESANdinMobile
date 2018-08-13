package com.batsamayi.ndincedetu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class FaultLog extends Handler_Image {

    /**
     * Keep track of the tasks to ensure we can cancel them requested.
     */
    Button bttLogFault;
    public Task_FaultLog taskFaultLog = null;
    public Task_SpinnerLoad taskSpinnerLoad = null;

    // UI references.
    private View prgFaultLog;
    private View frmFaultLog;

    private TextView txtFaultCategory;
    protected Spinner spnFaultCategory;
    protected SparseArray<FaultCategory> categories = new SparseArray<>(0);

    private TextView txtFaultItem;
    protected Spinner spnFaultItem;
    protected SparseArray<FaultItem> items = new SparseArray<>(0);

    private TextView txtFaultLocation;
    protected Spinner spnFaultLocation;
    protected SparseArray<FaultLocation> locations = new SparseArray<>(0);

    private EditText txtFloor;
    private EditText txtDescription;

    private int categoryID;
    private int itemID;
    private int floor;
    private String faultDescription;
    private int locationID;

    GPSTracker gpsTracker;
    private String gpsLocation;

    Fault fault;

    private void getValues() {
        faultDescription = txtDescription.getText().toString().trim();

        FaultCategory selectedCategory = new FaultCategory();
        selectedCategory.Description = spnFaultCategory.getSelectedItem().toString();
        for(int i = categories.size()-1; i >= 0; i--) {
            if(categories.valueAt(i).equals(selectedCategory)) {
                categoryID = categories.keyAt(i);
                break;
            }
        }

        FaultLocation selectedLocation = new FaultLocation();
        selectedLocation.Description = spnFaultLocation.getSelectedItem().toString();
        for(int i = locations.size()-1; i >= 0; i--) {
            if(locations.valueAt(i).equals(selectedLocation)) {
                locationID = locations.keyAt(i);
                break;
            }
        }

        FaultItem selectedItem = new FaultItem();
        selectedItem.Description = spnFaultItem.getSelectedItem().toString();
        for(int i = items.size()-1; i >= 0; i--) {
            if(items.valueAt(i).equals(selectedItem)) {
                itemID = items.keyAt(i);
                break;
            }
        }

        String temp = txtFloor.getText().toString().trim();
        if(isEmpty(temp)) {
            floor = 0;
            txtFloor.setText("0");
        } else
            floor = Integer.parseInt(temp);
        try {
            gpsLocation = gpsTracker.getLatitude() + ", " + gpsTracker.getLongitude();
        } catch (Exception e) {
            gpsTracker.showSettingsAlert();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myView = inflater.inflate(R.layout.fragment_fault_log, container, false);
        activity = (AppCompatActivity) getActivity();
        context = myView.getContext();
        activity.setTitle(R.string.logFault);
        frmFaultLog = myView.findViewById(R.id.frmFaultLog);
        prgFaultLog = myView.findViewById(R.id.prgFaultLog);
        txtFloor = myView.findViewById(R.id.txtFloor);
        txtDescription = myView.findViewById(R.id.txtDescription);
        txtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                TextView lblDescriptionLength = myView.findViewById(R.id.lblDescriptionLength);
                lblDescriptionLength.setVisibility(View.VISIBLE);
                String descritionLength = s.length() + "/100";
                lblDescriptionLength.setText(descritionLength);
            }
        });
        txtDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    myView.findViewById(R.id.lblDescriptionLength).setVisibility(View.GONE);
            }
        });

        gpsTracker = new GPSTracker(activity, activity);
        if(!gpsTracker.canGetLocation())
            gpsTracker.showSettingsAlert();

        if(!gpsTracker.canGetLocation())
            gpsTracker.showSettingsAlert();

        spnFaultCategory = myView.findViewById(R.id.spnFaultCategory);
        txtFaultCategory = (TextView)spnFaultCategory.getSelectedView();
        spnFaultCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Show subcategory when a category is selected
                if(id == 0) {
                    onNothingSelected(parent);
                } else {
                    spnFaultItem.setEnabled(true);

                    getValues();
                    Task_SpinnerLoad.setSpinnerAdapter(context, spnFaultItem, FaultItem.getSubset(categoryID));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                spnFaultItem.setEnabled(false);
                List<String> subItem = new ArrayList<String>() { { add(getString(R.string.prompt_category)); } };
                Task_SpinnerLoad.setSpinnerAdapter(context, spnFaultItem, subItem);

                try {
                    txtFaultCategory.setError(getString(R.string.error_field_required));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        spnFaultItem = myView.findViewById(R.id.spnFaultItem);
        spnFaultItem.setEnabled(false);
        txtFaultItem = (TextView) spnFaultItem.getSelectedView();

        spnFaultLocation = myView.findViewById(R.id.spnFaultLocation);
        txtFaultLocation = (TextView)spnFaultLocation.getSelectedView();

        Task_SpinnerLoad.run(this);

        directory = "Faults";

        img = myView.findViewById(R.id.imgFault);
        noImg = myView.findViewById(R.id.lblNoImage);
        myView.findViewById(R.id.lnrUploadPicture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });

        bttLogFault = myView.findViewById(R.id.bttLogFault);
        bttLogFault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { attemptLogFault(); }
        });

        return myView;
    }

    /**
     * Attempts to log the user's fault.
     */
    private void attemptLogFault() {
        if (taskFaultLog != null) {
            return;
        }

        txtFaultCategory = (TextView) spnFaultCategory.getSelectedView();
        txtFaultItem = (TextView) spnFaultItem.getSelectedView();
        txtFaultLocation = (TextView) spnFaultLocation.getSelectedView();
        TextView lblNoImage = frmFaultLog.findViewById(R.id.lblNoImage);

        txtFaultCategory.setError(null);
        txtFaultItem.setError(null);
        txtFaultLocation.setError(null);
        txtDescription.setError(null);
        txtFloor.setError(null);
        lblNoImage.setError(null);

        boolean cancel = false;
        View focusView = null;
        getValues();

        // Check if fault data is valid
        if (isEmpty(txtFloor.getText().toString().trim())) {
            txtFloor.setError(getString(R.string.error_field_required));
            focusView = txtFloor;
            cancel = true;
        } else if (categoryID < 0) {
            txtFaultCategory.setError(getString(R.string.error_field_required));
            focusView = spnFaultCategory;
            cancel = true;
        } else if (itemID < 0) {
            txtFaultItem.setError(getString(R.string.error_field_required));
            focusView = spnFaultItem;
            cancel = true;
        } else if (locationID < 0) {
            txtFaultLocation.setError(getString(R.string.error_field_required));
            focusView = spnFaultLocation;
            cancel = true;
        } else if (lblNoImage.getVisibility() == View.VISIBLE || pictureCompressed == null || pictureCompressed.length == 0) {
            lblNoImage.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if (cancel && focusView != null) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the fault log attempt.
            showProgress(true);
            if (isEmpty(faultDescription)) {
                String category = spnFaultCategory.getSelectedItem().toString();
                String item = spnFaultItem.getSelectedItem().toString();
                faultDescription = category + ", " + item;
            }

            Users user = Cookies.getInstance(context).userGetCurrent();
            fault = new Fault();
            fault.User = user.Id;
            fault.Description = faultDescription;
            fault.Status = 1;
            fault.Item = itemID;
            fault.GPS = gpsLocation;
            fault.Location = locationID;
            fault.Floor = floor;
            fault.Picture = pictureCompressed;

            Task_FaultLog.run(this);
        }
    }

    //Shows the progress UI and hides the log fault form.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        final View view = frmFaultLog;
        final View prgBar = prgFaultLog;
        try {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            view.setVisibility(show ? View.GONE : View.VISIBLE);
            view.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            prgBar.setVisibility(show ? View.VISIBLE : View.GONE);
            prgBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    prgBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } catch (Exception e){
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            prgBar.setVisibility(show ? View.VISIBLE : View.GONE);
            view.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onImageLoaded() {
        bttLogFault.setEnabled(true);
    }
}
