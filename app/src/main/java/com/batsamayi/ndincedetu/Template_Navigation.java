package com.batsamayi.ndincedetu;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class Template_Navigation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    public static FileDownloadTask downloadTask;
    NavigationView navigationView;

    static void setProfilePicture(int userID, final ImageView imageView, boolean forceCloud) {
        final String filename = File.separator + "User" + userID + ".jpg";
        final String localPath = imageView.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "Avatars" + filename;
        final File localFile = new File(localPath);
        try {
            final Bitmap bmp = BitmapFactory.decodeFile(localFile.getAbsolutePath());
            if (bmp != null)
                setImage(imageView, bmp);
            if (bmp == null || forceCloud && downloadTask != null) {
                StorageReference Ref = FirebaseStorage.getInstance().getReference().child("Avatars" + filename);
                final File tempFile = File.createTempFile("NdincedeCloud", ".jpg");
                downloadTask = Ref.getFile(tempFile);
                downloadTask.addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bmp = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                                setImage(imageView, bmp);
                                try (FileChannel fileFrom = new FileInputStream(tempFile).getChannel()) {
                                    try (FileChannel fileTo = new FileOutputStream(localFile).getChannel()) {
                                        try {
                                            fileFrom.transferTo(0, fileFrom.size(), fileTo);
                                        } catch (Exception ignored) {
                                        }
                                    }
                                } catch (Exception ignored) { }
                                downloadTask = null;
                            }
                        });
            }
        } catch (Exception ignored) { }
    }

    private static void setImage(ImageView imageView, Bitmap bmp) {
        if (bmp.getHeight() == bmp.getWidth()) {
            RoundImage roundedImage = new RoundImage(bmp);
            imageView.setImageDrawable(roundedImage);
        } else {
            imageView.setImageBitmap(bmp);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_navigation);

        //if not logged in
        Users user = Cookies.getInstance(this).userGetCurrent();
        if(user.Username.equals("") || user.Id <= 0) {
            Cookies.getInstance(this).logout();
            startActivity(new Intent(this, UserLogin.class));
            return;
        }

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadContent(new UserProfile(), null);
            }
        });
        ImageView imgProfile = headerView.findViewById(R.id.imgNavProfile);
        setProfilePicture(user.Id, imgProfile, false);
        TextView txtUsername = headerView.findViewById(R.id.txtNavUsername);
        txtUsername.setText(user.Username);
        TextView txtFullName = headerView.findViewById(R.id.txtNavFullName);
        String fullName = "(" + user.LastName + ", " + user.FirstName + ")";
        txtFullName.setText(fullName);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawer.closeDrawers();

        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_faults_all));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(getFragmentManager().getBackStackEntryCount() == 0)
                super.onBackPressed();
            else
                getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_notifications:
                loadContent(new Notifications(), item);
                break;
            case R.id.nav_fault_log:
                loadContent(new FaultLog(), item);
                break;
            case R.id.nav_faults_all:
                loadContent(new FaultsAll(), item);
                break;
            case R.id.nav_about:
                loadContent(About.class);
                break;
            case R.id.nav_contact:
                loadContent(Contact.class);
                break;
            case R.id.nav_logout:
                AlertDialog.Builder logoutPrompt = new AlertDialog.Builder(this);
                logoutPrompt.setMessage("Are you sure?")
                        .setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Cookies.getInstance(getApplicationContext()).logout();
                                loadContent(UserLogin.class);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                drawer.closeDrawers();
                            }
                        }).create().show();
                break;
        }
        return true;
    }

    private void loadContent(Fragment content, MenuItem menuItem) {
        getFragmentManager().beginTransaction().replace(R.id.content_frame, content).commit();
        drawer.closeDrawer(GravityCompat.START);
        if (menuItem != null) {
            menuItem.setChecked(true);
            navigationView.setItemIconTintList(null);
        } else
            uncheckAll();
    }

    private void loadContent(Class<?> content) {
        uncheckAll();
        startActivity(new Intent(getApplicationContext(), content));
    }

    private void uncheckAll() {
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i++);
            menuItem.setChecked(false);
        }
    }
}
