package com.batsamayi.ndincedetu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FaultsAll extends Recycler_Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_fauts_all, container, false);
        type = PREVIEW;
        super.onCreateView(inflater, container, savedInstanceState);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setTitle(getString(R.string.yourFaults));

        return myView;
    }
}
