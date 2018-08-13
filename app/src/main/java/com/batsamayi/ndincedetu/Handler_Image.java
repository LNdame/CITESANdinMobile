package com.batsamayi.ndincedetu;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import static android.app.Activity.RESULT_OK;

abstract class Handler_Image extends Fragment {
    private static final int REQUEST_IMAGES = 100;
    private static final int REQUEST_GALLERY = 101;
    ImageView img;
    private static final int REQUEST_CAMERA = 102;
    Context context;
    AppCompatActivity activity;
    View noImg;
    String directory;

    abstract void showProgress(boolean show);

    //volatile byte[] picture;
    volatile byte[] pictureCompressed;
    Task_ImageLoad taskImageLoad;
    Uri imageUri;

    public void getPicture() {
        PermissionHandler ph = new PermissionHandler(activity);
        if (ph.needsPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) && ph.needsPermission(android.Manifest.permission.CAMERA))
            ph.requestPermission(REQUEST_IMAGES, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA);
        else if (ph.needsPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
            ph.requestPermission(REQUEST_GALLERY, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        else if (ph.needsPermission(android.Manifest.permission.CAMERA))
            ph.requestPermission(REQUEST_CAMERA, Manifest.permission.CAMERA);

        new AlertDialog.Builder(context).setMessage("Select Image From")
                .setCancelable(true)
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        String packageName = context.getApplicationContext().getPackageName();
                        File tempFile = getOutputMediaFile("preCrop");
                        imageUri = null;
                        if (tempFile != null) {
                            try {
                                imageUri = FileProvider.getUriForFile(context, packageName, tempFile);
                            } catch (Exception e) {
                                imageUri = Uri.fromFile(tempFile);
                            }
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivityForResult(cameraIntent, REQUEST_CAMERA);
                        } else {
                            Toast.makeText(context, "Camera not supported", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryIntent.setType("image/*");
                        startActivityForResult(galleryIntent, REQUEST_GALLERY);
                    }
                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        if (resultCode == RESULT_OK) {
            if (noImg != null) {
                img.setVisibility(View.INVISIBLE);
                noImg.setVisibility(View.VISIBLE);
            }
            try {
                switch (requestCode) {
                    case REQUEST_GALLERY:
                        cropPicture(imageReturnedIntent.getData());
                        break;
                    case REQUEST_IMAGES:
                        getOutputMediaFile("preCrop").delete();
                    case REQUEST_CAMERA:
                        Task_ImageLoad.run(this);
                        break;
                }
            } catch (Exception e) {
                img.setVisibility(View.VISIBLE);
                if (noImg != null)
                    noImg.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void cropPicture(Uri incoming) {
        try {
            imageUri = Uri.fromFile(getOutputMediaFile("postCrop"));
            // call the standard crop action intent (the user device may not
            // support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(incoming, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("scaleUpIfNeeded", true);
            // retrieve data on return
            cropIntent.putExtra("return-data", false);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, REQUEST_IMAGES);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            Toast.makeText(context, "This device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
        }
    }

    File getOutputMediaFile(String name) {
        File storageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), directory);
        if (!storageDir.exists() && !storageDir.mkdirs())
            return null;
        return new File(storageDir.getPath() + File.separator + name + ".jpg");
    }

    public abstract void onImageLoaded();
}
