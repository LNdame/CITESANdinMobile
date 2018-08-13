package com.batsamayi.ndincedetu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public abstract class Recycler_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public final int PREVIEW = 100;
    public final int NOTIFICATION = 101;
    public int type;

    View myView;
    public RecyclerView rcyData;
    public TextView txtNoData;

    public Task_FaultGet taskGetData = null;

    public RecyclerView.Adapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    //Shows the progress UI and hides the logged faults.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        final ConstraintLayout lytData = myView.findViewById(R.id.lytData);
        final ProgressBar progressBar = myView.findViewById(R.id.progressBar);

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        try {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            lytData.setVisibility(show ? View.GONE : View.VISIBLE);
            lytData.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    lytData.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } catch (Exception e) {
            Log.e("RECYCLER_FRAGMENT", "Show Progress Error" + e.getMessage());
            /*AlertDialog.Builder logoutPrompt = new AlertDialog.Builder(myView.getContext());
            logoutPrompt.setTitle("ERROR")
                    .setMessage(e.getMessage())
                    .setPositiveButton("OK", null)
                    .create()
                    .show();*/
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        Context context = myView.getContext();

        setHasOptionsMenu(true);

        rcyData = myView.findViewById(R.id.rcyCard);
        txtNoData = myView.findViewById(R.id.txtNoData);

        // changes in content do not change the LAYOUT size of the RecyclerView
        rcyData.setHasFixedSize(false);

        // use a linear LAYOUT manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        rcyData.setLayoutManager(mLayoutManager);
        rcyData.setItemAnimator(new DefaultItemAnimator());

        adapter = new Adapter_Fault(new SparseArray<Fault>(0));
        rcyData.setVisibility(View.GONE);
        rcyData.setAdapter(adapter);

        // SwipeRefreshLayout
        swipeRefreshLayout = myView.findViewById(R.id.lytSwipe);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent,
                R.color.colorPrimaryDark, R.color.colorAccentDark);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                loadRecyclerViewData();
            }
        });
        loadRecyclerViewData();

        FloatingActionButton fab = myView.findViewById(R.id.fltLogFault);
        if(fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.getFragmentManager().beginTransaction().replace(R.id.content_frame, new FaultLog()).commit();
                }
            });
        }
        return myView;
    }

    @Override
    public void onRefresh() { loadRecyclerViewData(); }

    public void loadRecyclerViewData() {
        Task_FaultGet.run(this);
    }
}
