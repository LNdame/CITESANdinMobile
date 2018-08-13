package com.batsamayi.ndincedetu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class UserProfile extends Handler_Image {
    Task_AvatarUpload taskAvatarUpload;
    Users user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myView = inflater.inflate(R.layout.fragment_user_profile, container, false);
        activity = (AppCompatActivity) getActivity();
        context = myView.getContext();
        activity.setTitle(R.string.profile);

        user = Cookies.getInstance(context).userGetCurrent();
        TextView txtUsername = myView.findViewById(R.id.txtUsername);
        txtUsername.setText(user.Username);
        TextView txtFullName = myView.findViewById(R.id.txtFullName);
        String fullName = "(" + user.LastName + ", " + user.FirstName + ")";
        txtFullName.setText(fullName);

        directory = "Avatars";
        img = myView.findViewById(R.id.imgProfile);
        Template_Navigation.setProfilePicture(user.Id, img, true);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });
        myView.findViewById(R.id.imgUploadImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });
        return myView;
    }

    //Shows the progress UI and hides the log fault form.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        final View prgBar = activity.findViewById(R.id.prgProfilePicture);
        try {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

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
        }
    }

    @Override
    public void onImageLoaded() {
        user.Picture = pictureCompressed;
        Task_AvatarUpload.run(this);
        Template_Navigation.setProfilePicture(user.Id, img, true);
    }
}
