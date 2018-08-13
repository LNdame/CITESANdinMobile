package com.batsamayi.ndincedetu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

abstract class Adapter_Card extends RecyclerView.Adapter<Adapter_Card.ViewHolder> {
    private final SparseArray<Fault> faultArray;
    Fault fault;
    CardView view;
    Bitmap bmp;

    private final String LOGGED = "Logged";
    private final String OPENED = "Opened";
    private final String CLOSED = "Closed";
    String statusText = OPENED;

    private final int TEXT = 0;
    private final int IMAGE = 1;
    private final int PROGRESS = 2;

    // Replace the contents of a view (invoked by the LAYOUT manager)
    public void onBindViewHolder(@NonNull final Adapter_Card.ViewHolder holder, int position) {
        fault = faultArray.valueAt(faultArray.size() - position - 1);

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            int faultID = fault.ID;

            if(fault.Picture == null) {
                holder.showImage(PROGRESS);
                final String filename = File.separator + "Fault" + faultID + ".jpg";
                final File localPath = new File(holder.mView.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "Faults");
                final boolean mkdirs = localPath.mkdirs();
                final File localFile = new File(localPath.getAbsolutePath() + filename);

                try {
                    pictureToByte(localFile);

                    bmp = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    if(bmp != null/* && holder.imgFault.getDrawable() != null*/) {
                        holder.showImage(IMAGE);
                    } else {
                        StorageReference Ref = holder.mStorageRef.child("Faults" + filename);
                        final File tempFile = File.createTempFile("NdincedeCloud", ".jpg");
                        Ref.getFile(tempFile)
                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        bmp = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                                        holder.showImage(IMAGE);
                                        try (FileChannel streamFrom = new FileInputStream(tempFile).getChannel()) {
                                            pictureToByte(tempFile);
                                            localFile.createNewFile();
                                            if (true) {
                                                try (FileChannel streamTo = new FileOutputStream(localFile).getChannel()) {
                                                    try {
                                                        streamFrom.transferTo(0, streamFrom.size(), streamTo);
                                                    } catch (Exception e) {
                                                        holder.showImage(TEXT);
                                                    }
                                                }
                                            }
                                        } catch (IOException ignored) {
                                            holder.showImage(TEXT);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                holder.showImage(TEXT);
                            }
                        });
                    }
                } catch (Exception e) {
                    holder.showImage(TEXT);
                }
            }
            int status = fault.Status;
            Context context = holder.imgStatus.getContext();
            if (status == 1) {
                int statusColor = ContextCompat.getColor(context, android.R.color.holo_red_dark);
                holder.imgStatus.setColorFilter(statusColor, android.graphics.PorterDuff.Mode.MULTIPLY);
                statusText = LOGGED;
            } else if (status == 2) {
                statusText = OPENED;
            } else if (status == 3) {
                holder.imgStatus.setImageDrawable(context.getDrawable(R.drawable.ic_launcher_green));
                statusText = CLOSED;
            }
            holder.txtStatus.setText(statusText);

            holder.imgStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.txtStatus.setVisibility(View.VISIBLE);
                    holder.imgStatus.setVisibility(View.GONE);
                    new CountDownTimer(Toast.LENGTH_LONG * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) { }

                        @Override
                        public void onFinish() {
                            holder.txtStatus.setVisibility(View.GONE);
                            holder.imgStatus.setVisibility(View.VISIBLE);
                        }
                    }.start();
                }
            });
            holder.txtStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.txtStatus.setVisibility(View.GONE);
                    holder.imgStatus.setVisibility(View.VISIBLE);
                }
            });

            setDescription(holder.txtDescription, fault);
        } catch (Exception e) {
            view.setVisibility(View.GONE);
        }
    }

    private void pictureToByte(File localFile) {
        try {
            RandomAccessFile f = new RandomAccessFile(localFile, "r");
            fault.Picture = new byte[(int) f.length()];
            f.readFully(fault.Picture);
        } catch (Exception ignored) {}
    }

    Adapter_Card(SparseArray<Fault> faultArray) {
        this.faultArray = faultArray;

    }

    // Create new views (invoked by the LAYOUT manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        view = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(getLayout(), parent, false);
        return new ViewHolder(view);
    }

    abstract int getLayout();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        // each data item is just a string in this case
        final ProgressBar prgImage;
        final TextView txtNoImage;
        final ImageView imgFault;
        final ImageView imgStatus;
        final TextView txtStatus;
        final TextView txtDescription;
        private final StorageReference mStorageRef;

        ViewHolder(View view) {
            super(view);
            mView = view;

            prgImage = view.findViewById(R.id.prgImage);
            txtNoImage = view.findViewById(R.id.txtNoImage);
            imgFault = view.findViewById(R.id.imgFault);
            imgStatus = view.findViewById(R.id.imgStatus);
            txtStatus = view.findViewById(R.id.txtStatus);
            txtDescription = view.findViewById(R.id.txtDescription);

            mStorageRef = FirebaseStorage.getInstance().getReference();
        }

        private void showImage(int mode) {
            View view = txtNoImage;
            switch (mode) {
                case IMAGE:
                    view = imgFault;
                    imgFault.setImageBitmap(bmp);
                    bmp = null;
                    break;
                case PROGRESS:
                    view = prgImage;
                    break;
            }
            txtNoImage.setVisibility(View.INVISIBLE);
            imgFault.setVisibility(View.INVISIBLE);
            prgImage.setVisibility(View.INVISIBLE);
            view.setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("deprecation")
    abstract void setDescription(TextView textView, Fault fault);

    // Return the size of your dataset (invoked by the LAYOUT manager)
    @Override
    public int getItemCount() { return faultArray.size(); }
}
