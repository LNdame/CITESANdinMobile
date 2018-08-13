package com.batsamayi.ndincedetu;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

class Task_ImageLoad extends AsyncTask<Void, Void, Boolean> {
    private final WeakReference<Handler_Image> activity;

    private Task_ImageLoad(Handler_Image activity) {
        this.activity = new WeakReference<>(activity);
    }

    public static void run(Handler_Image handler) {
        if (handler.taskImageLoad == null) {
            // Show a progress spinner, and kick off a background task to
            // perform the user registration attempt.
            ImageView imageView = handler.img;
            View noImage = handler.noImg;

            imageView.setImageURI(null);
            imageView.setImageURI(handler.imageUri);

            imageView.setVisibility(View.VISIBLE);
            if (noImage != null)
                noImage.setVisibility(View.INVISIBLE);
            handler.taskImageLoad = new Task_ImageLoad(handler);
            handler.showProgress(true);
            handler.taskImageLoad.execute((Void) null);
        }
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Handler_Image caller = activity.get();
        //caller.picture = null;
        caller.pictureCompressed = null;

        try {
            Bitmap bmp = ((BitmapDrawable) caller.img.getDrawable()).getBitmap();
            try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //caller.picture = stream.toByteArray();
            }

            int bmpHeight = bmp.getHeight();
            int bmpWidth = bmp.getWidth();
            if(bmpHeight > 1500) {
                bmpWidth /= bmpHeight/1500f;
                bmpHeight = 1500;
            }
            if(bmpWidth > 1500) {
                bmpHeight /= bmpWidth/1500f;
                bmpWidth = 1500;
            }
            bmp = Bitmap.createScaledBitmap(bmp, bmpWidth, bmpHeight, true);
            float fileSize = bmpHeight*bmpWidth*8f/1024f/1024f;
            int quality = 100;
            if(fileSize > 4)
                quality /= fileSize / 4;

            try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
                bmp.compress(Bitmap.CompressFormat.JPEG, quality, stream);
                caller.pictureCompressed = stream.toByteArray();
                if (!bmp.isRecycled()) {
                    bmp = null;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (!success)
            new AlertDialog.Builder(activity.get().getActivity()).setTitle("ERROR")
                    .setMessage("Image functionality is currently unstable. Please contact the developer if this issue persists.")
                    .setPositiveButton("OK", null).create().show();
        finish();
    }

    @Override
    protected void onCancelled() {
        finish();
    }

    private void finish() {
        Handler_Image caller = activity.get();
        caller.showProgress(false);
        caller.taskImageLoad = null;
        caller.onImageLoaded();
    }
}
