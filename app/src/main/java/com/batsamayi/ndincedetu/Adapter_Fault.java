package com.batsamayi.ndincedetu;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

public class Adapter_Fault extends Adapter_Card {

    // Replace the contents of a view (invoked by the LAYOUT manager)
    @Override
    public void onBindViewHolder(@NonNull final Adapter_Card.ViewHolder superHolder, int position) {
        ViewHolder holder = new ViewHolder(view);
        super.onBindViewHolder(superHolder, position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        try {
            //FaultLocation.setText(holder.txtLocation, fault[position].Location, Spinnable.LOCATION);
            //FaultItem.setText(holder.txtCategory, fault[position].Item, Spinnable.CATEGORY);
            //FaultItem.setText(holder.txtItem, fault[position].Item, Spinnable.ITEM);
            holder.txtRefNo.setText(fault.RefNo);

            holder.txtLocation.setText(FaultLocation.knownSpinnables.get(fault.Location).toString());
            FaultItem faultItem = FaultItem.knownSpinnables.get(fault.Item);
            holder.txtCategory.setText(FaultCategory.knownSpinnables.get(faultItem.FaultCategoryID).toString());
            holder.txtItem.setText(faultItem.toString());

            String floor = "Floor " + fault.Floor;
            holder.txtFloor.setText(floor);
            try {
                String date = fault.DateLogged.split(" ")[0];
                holder.txtDate.setText(date);
            } catch (Exception e) {
                holder.txtDate.setText("");
            }
        } catch (Exception e) {
            holder.mView.setVisibility(View.GONE);
        }
    }

    Adapter_Fault(SparseArray<Fault> fault) {
        super(fault);
    }

    @Override
    int getLayout() { return R.layout.card_fault; }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        // each data item is just a string in this case
        final TextView txtLocation;
        final TextView txtCategory;
        final TextView txtItem;
        final TextView txtFloor;
        final TextView txtDate;
        final TextView txtRefNo;

        ViewHolder(View view) {
            super(view);
            mView = view;

            txtLocation = view.findViewById(R.id.txtLocation);
            txtCategory = view.findViewById(R.id.txtCategory);
            txtItem = view.findViewById(R.id.txtItem);
            txtFloor = view.findViewById(R.id.txtFloor);
            txtDate = view.findViewById(R.id.txtDate);
            txtRefNo = view.findViewById(R.id.txtRefNo);
        }
    }

    @SuppressWarnings("deprecation")
    void setDescription(TextView textView, Fault fault) {
        String description = "<strong>Description: </strong>" + fault.Description;
        if (Build.VERSION.SDK_INT >= 24)
            textView.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY)); // for 24 api and more
        else
            textView.setText(Html.fromHtml(description)); // or for older api
    }
}
