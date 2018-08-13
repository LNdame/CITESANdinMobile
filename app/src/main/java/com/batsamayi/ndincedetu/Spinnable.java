package com.batsamayi.ndincedetu;

import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

abstract class Spinnable implements KvmSerializable {
    public int Id;
    public String Description;

    static final int ITEM = R.string.error_item_not_found;
    static final int CATEGORY = R.string.error_category_not_found;
    static final int LOCATION = R.string.error_location_not_found;
    static final int STATUS = R.string.error_status_not_found;

    Spinnable() {
        Id = -1;
        Description = "Error";
    }

    @Override
    public Object getProperty(int index) {
        switch (index) {
            case 0: return Id;
            case 1: return Description;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 2;
    }

    @Override
    public void setProperty(int index, Object value) {
        switch (index) {
            case 0: Id = Integer.parseInt(value.toString()); break;
            case 1: Description = value.toString(); break;
        }
    }

    @Override
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        switch (index) {
            case 0:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "Id";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Description";
                break;
        }
    }

    public String toString() {
        return Description;
    }

    public boolean equals(Object otherObject) {
        if(getClass() != otherObject.getClass()) return false;
        Spinnable other = (Spinnable)otherObject;
        boolean matchingDesc = Description.equals(other.Description);
        boolean matchingId = Id == other.Id;
        return matchingDesc || matchingId;
    }

    public static void setText(final View view, final int spinnableID, final int type) {
        final CharSequence loading = view.getContext().getResources().getString(R.string.alert_loading);
        try {
            ((TextView) view).setText(loading);
        } catch (Exception ignored) { }
        try {
            setTextFromArray(view, spinnableID, type);
        } catch (Exception ignored) { }

        boolean done = false;
        try {
            done = ((TextView) view).getText().equals(loading);
        } catch (Exception ignored) { }
        if (!done) {
            new Thread() {
                public void run() {
                    switch (type) {
                        case ITEM:
                            new ServerConnector().getFaultItems();
                            break;
                        case CATEGORY:
                            new ServerConnector().getFaultCategories();
                            break;
                        case LOCATION:
                            new ServerConnector().getFaultLocations();
                            break;
                        case STATUS:
                            new ServerConnector().getFaultStatuses();
                            break;
                    }
                    try {
                        setTextFromArray(view, spinnableID, type);
                    } catch (Exception ignored) { }
                    try {
                        if (((TextView) view).getText().equals(loading))
                            ((TextView) view).setText(type);
                    } catch (Exception ignored) { }
                }
            }.start();
        }
    }

    private static void setTextFromArray(View view, int spinnableID, int type) {
        try {
            switch (type) {
                case ITEM:
                    ((TextView) view).setText(FaultItem.knownSpinnables.get(spinnableID).Description);
                    break;
                case CATEGORY:
                    ((TextView) view).setText(FaultCategory.knownSpinnables.get(spinnableID).Description);
                    break;
                case LOCATION:
                    ((TextView) view).setText(FaultLocation.knownSpinnables.get(spinnableID).Description);
                    break;
                case STATUS:
                    ((TextView) view).setText(FaultStatus.knownSpinnables.get(spinnableID).Description);
                    break;
            }
        } catch (Exception ignored) { }
    }
}