package com.batsamayi.ndincedetu;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

public class Adapter_Notification extends Adapter_Card {

    // Replace the contents of a view (invoked by the LAYOUT manager)
    @Override
    public void onBindViewHolder(@NonNull final Adapter_Card.ViewHolder holder, int position) {
        super.onBindViewHolder(new Adapter_Card.ViewHolder(view), position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    Adapter_Notification(SparseArray<Fault> fault) {
        super(fault);
    }

    @Override
    int getLayout() { return R.layout.card_notification; }

    void setDescription(TextView textView, Fault fault) {
        String description = "Fault <strong>" + fault.RefNo + "</strong> is now " + statusText;
        if (Build.VERSION.SDK_INT >= 24)
            textView.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY)); // for 24 api and more
        else
            textView.setText(Html.fromHtml(description)); // or for older api
    }
}
