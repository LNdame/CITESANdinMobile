package com.batsamayi.ndincedetu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class About extends AppCompatActivity {

    String version = "UNKNOWN";
    String companyInfo = "©Batsamayi 2015 - 2018";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(R.string.about);

        try {
            version = "v " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            Dialog_Modal.newInstance("").show(getFragmentManager(), "VERSIONERROR");
        }

        //((TextView) findViewById(R.id.lblVersionName)).setText(version);
        //((TextView) findViewById(R.id.lblCompanyInfo)).setText(companyInfo);
    }
}
