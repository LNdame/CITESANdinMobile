package com.batsamayi.ndincedetu;

import android.util.SparseArray;
import android.widget.TextView;

import org.ksoap2.serialization.PropertyInfo;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class FaultItem extends Spinnable {
    public static SparseArray<FaultItem> knownSpinnables;

    public int FaultCategoryID;

    FaultItem() { FaultCategoryID = -1; }

    @Override
    public Object getProperty(int index) {
        switch (index) {
            case 2: return FaultCategoryID;
            default: return super.getProperty(index);
        }
    }

    @Override
    public int getPropertyCount() {
        return 3;
    }

    @Override
    public void setProperty(int index, Object value) {
        switch (index) {
            case 2: FaultCategoryID = Integer.parseInt(value.toString()); break;
            default: super.setProperty(index, value);
        }
    }

    @Override
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        switch (index) {
            case 2:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "FaultCategoryID";
                break;
            default:
                super.getPropertyInfo(index, properties, info);
                break;
        }
    }

    protected static void fromConcat(Object concatObject) {
        String concat = concatObject.toString();
        //Each row of the 2-dimensional array is separated by a #
        String[] rows = concat.split("#");
        knownSpinnables = new SparseArray<>(rows.length + 1);

        //Set the prompt for the spinner.
        knownSpinnables.put(-1, new FaultItem());
        knownSpinnables.get(-1).Description = "Please select an item";
        for (String row : rows) {
            //Each column of the array is separated by a %
            String[] column = row.split("%");
            int id = Integer.parseInt(column[0]);
            knownSpinnables.put(id, new FaultItem());
            knownSpinnables.get(id).Id = id;
            knownSpinnables.get(id).Description = column[1];
            knownSpinnables.get(id).FaultCategoryID = Integer.parseInt(column[2]);
        }
    }

    public static void setText(final TextView textView, final int spinnableID, final int type) {
        textView.setText(R.string.alert_loading);
        final CharSequence loading = textView.getText();
        try {
            setTextFromArray(textView, spinnableID, type);
        } catch (Exception ignored) {
        }

        if (textView.getText().equals(loading)) {
            new Thread() {
                public void run() {
                    try {
                        setTextFromArray(textView, spinnableID, type);
                    } catch (Exception ignored) { }
                    if (textView.getText().equals(loading))
                        textView.setText(type);
                }
            }.start();
        }
    }

    private static void setTextFromArray(TextView textView, int spinnableID, int type) {
        FaultItem spinnable = knownSpinnables.get(spinnableID);
        if (spinnable != null) {
            if (type == Spinnable.CATEGORY)
                FaultCategory.setText(textView, spinnable.FaultCategoryID, type);
            else
                textView.setText(spinnable.Description);
        }
    }

    public static List<String> getSubset(int categoryID) {
        ArrayList<String> subItem = new ArrayList<>();
        subItem.add("Please select an item");
        for (int i = 0; i < knownSpinnables.size(); i++) {
            FaultItem currentItem = knownSpinnables.valueAt(i);
            if (currentItem.FaultCategoryID == categoryID)
                subItem.add(currentItem.Description);
        }
        return subItem;
    }
}
