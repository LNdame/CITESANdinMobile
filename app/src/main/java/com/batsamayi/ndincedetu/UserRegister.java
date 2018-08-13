package com.batsamayi.ndincedetu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.Manifest.permission.READ_CONTACTS;

public class UserRegister extends Handler_Verification implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the User Registration tasks to ensure we can cancel it if requested.
     */
    Task_UserRegister taskUserRegister = null;
    Task_UserModify taskUserModify = null;

    // UI references.
    private EmailAddressView txtUsername;
    private PasswordView txtPassword;
    private PasswordView txtPasswordConfirm;
    private View mProgressView;
    private View mRegisterFormView;
    private AutoCompleteTextView txtNameFirst;
    private AutoCompleteTextView txtNameLast;

    private String username;
    private String password;
    private String passwordConfirm;
    private String nameFirst;
    private String nameLast;
    public void getValues() {
        super.getValues();
        username = txtUsername.getText();
        password = txtPassword.getText();
        passwordConfirm = txtPasswordConfirm.getText();
        nameFirst = txtNameFirst.getText().toString().trim();
        nameLast = txtNameLast.getText().toString().trim();
    }

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        Cookies cookies = Cookies.getInstance(this);
        user = cookies.userGetCurrent();

        Intent intent = getIntent();
        context = getBaseContext();

        txtUsername = findViewById(R.id.txtUsername);
        populateAutoComplete();
        txtPassword = findViewById(R.id.txtPassword);
        txtPasswordConfirm = findViewById(R.id.txtPasswordConfirm);
        txtNameFirst = findViewById(R.id.txtNameFirst);
        txtNameLast = findViewById(R.id.txtNameLast);

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

        if(intent.hasExtra(R.string.package_name + ".username"))
            txtUsername.setText(Objects.requireNonNull(intent.getExtras()).getString(R.string.package_name + ".username"));
        if(intent.hasExtra(R.string.package_name + ".password"))
            txtPassword.setText(intent.getExtras().getString(R.string.package_name + ".password"));

        findViewById(R.id.bttRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            attemptRegister();
            }
        });


        findViewById(R.id.txtAlreadyRegistered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //go back to previous activity
            finish();
            }
        });

        //if the register page is opened, but the user is logged in
        if (!user.Username.equals("")) {
            if (user.FirstName.equals("")) { //if the user has not provided a name
                showModificationForm();
            } else if (!user.IsApproved) { //if the user is not verified
                showVerificationForm();
            } else {
                startActivity(new Intent(this, Template_Navigation.class));
                finish();
            }
        } else {
            showCreationForm();
        }
    }

    private void showCreationForm() {
        lnrUserCreate = findViewById(R.id.lnrUserCreate);
        lnrUserCreate.setVisibility(View.VISIBLE);
        lnrUserModify = findViewById(R.id.lnrUserModify);
        lnrUserModify.setVisibility(View.GONE);
        lnrVerifierForm = findViewById(R.id.lnrVerifierForm);
        lnrVerifierForm.setVisibility(View.GONE);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(txtUsername, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Attempts to register the account specified by the register form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual registration attempt is made.
     */
    private void attemptRegister() {
        getValues();
        if (taskUserVerify != null) {
            return;
        }

        // Reset errors.
        txtUsername.setError(null);
        txtPassword.setError(null);
        txtPasswordConfirm.setError(null);

        if (txtUsername.isValid() && txtPassword.isValid() && txtPasswordConfirm.isValid(txtPassword)) {
            // Store values at the time of the registration attempt.
            getValues();

            user.Username = username;
            user.Password = password;
            //Check if input is a number. This is currently the best way to check if someone is a student, or a staff member
            if (username.split("#")[0].matches("\\d+"))
                user.UserType = "student";
            else
                user.UserType = "staff";
            // Show a progress spinner, and kick off a background task to
            showProgress(true);
            // perform the user registration attempt.
            Task_UserRegister.run(this, user);
        }
    }

    /**
     * Shows the progress UI and hides the registration form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), UserRegister.ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            emails.add(cursor.getString(ProfileQuery.IS_PRIMARY));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(UserRegister.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        txtUsername.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    public void showModificationForm() {
        lnrUserCreate = findViewById(R.id.lnrUserCreate);
        lnrUserCreate.setVisibility(View.GONE);
        lnrUserModify = findViewById(R.id.lnrUserModify);
        lnrUserModify.setVisibility(View.VISIBLE);
        lnrVerifierForm = findViewById(R.id.lnrVerifierForm);
        lnrVerifierForm.setVisibility(View.GONE);

        txtNameFirst.requestFocus();

        findViewById(R.id.bttProceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptModify();
            }
        });
    }

    private void attemptModify() {
        if (taskUserModify != null) {
            return;
        }

        // Reset errors.
        txtNameFirst.setError(null);
        txtNameLast.setError(null);

        // Store values at the time of the registration attempt.
        getValues();

        boolean cancel = false;
        View focusView = null;

        // Check that name and surname have been entered
        if (TextUtils.isEmpty(nameFirst)) {
            txtNameFirst.setError(getString(R.string.error_field_required));
            focusView = txtNameFirst;
            cancel = true;
        } else if (TextUtils.isEmpty(nameLast)) {
            txtNameLast.setError(getString(R.string.error_field_required));
            focusView = txtNameLast;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt registration and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user registration attempt.
            showProgress(true);
            user.Username = username;
            user.FirstName = nameFirst;
            user.LastName = nameLast;
            Task_UserModify.run(this, user);
        }
    }

    private LinearLayout lnrUserCreate;
    private LinearLayout lnrUserModify;

    public void showVerificationForm() {
        lnrUserCreate = findViewById(R.id.lnrUserCreate);
        lnrUserCreate.setVisibility(View.GONE);
        lnrUserModify = findViewById(R.id.lnrUserModify);
        lnrUserModify.setVisibility(View.GONE);
        super.showVerificationForm();
        ((TextView) findViewById(R.id.lblPrompt)).setText(R.string.prompt_user_modify);
    }

    @Override
    public void onBackPressed() {
        getValues ();
        Intent startIntent = new Intent(context, UserLogin.class);
        startIntent.putExtra(R.string.package_name + ".username", username);
        startActivity(startIntent);
    }

    @Override
    public void onCompleteVerificationTask(String verifyResult) {
        Cookies.getInstance(this).logout();
        Intent startIntent = new Intent(context, UserLogin.class);
        startIntent.putExtra(R.string.package_name + ".username", user.Username);
        startActivity(startIntent);
        finish();
    }
}
