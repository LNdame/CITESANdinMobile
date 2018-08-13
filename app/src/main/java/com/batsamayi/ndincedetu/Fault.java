package com.batsamayi.ndincedetu;

import android.util.Base64;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

public class Fault implements KvmSerializable {

    public int ID;
    public String Description;
    public byte[] Picture;
    public String RefNo;
    public int Status;
    public int Item;
    public int Floor;
    public String GPS;
    public int Location;
    public int User;
    public String DateLogged;
    public String DateOpened;
    public String DateClosed;

    public Fault() {
        ID = -1;
    }

    @Override
    public Object getProperty(int index) {
        switch (index) {
            case 0: return ID;
            case 1: return Description;
            case 2: return Picture;
            case 3: return RefNo;
            case 4: return Status;
            case 5: return Item;
            case 6: return Floor;
            case 7: return GPS;
            case 8: return Location;
            case 9: return User;
            case 10: return DateLogged;
            case 11: return DateOpened;
            case 12: return DateClosed;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 13;
    }

    @Override
    public void setProperty(int index, Object value) {
        switch (index) {
            case 0: ID = Integer.parseInt(value.toString()); break;
            case 1: Description = value.toString(); break;
            case 2: Picture = Base64.decode(value.toString(), Base64.DEFAULT); break;
            case 3: RefNo = value.toString(); break;
            case 4: Status = Integer.parseInt(value.toString()); break;
            case 5: Item = Integer.parseInt(value.toString()); break;
            case 6: Floor = Integer.parseInt(value.toString()); break;
            case 7: GPS = value.toString(); break;
            case 8: Location = Integer.parseInt(value.toString()); break;
            case 9: User = Integer.parseInt(value.toString()); break;
            case 10: DateLogged = value.toString(); break;
            case 11: DateOpened = value.toString(); break;
            case 12: DateClosed = value.toString(); break;
        }
    }

    @Override
    public void getPropertyInfo(int index, Hashtable properties, PropertyInfo info) {
        switch (index) {
            case 0:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "ID";
                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Description";
                break;
            case 2:
                info.type = MarshalBase64.BYTE_ARRAY_CLASS;
                info.name = "Picture";
                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "RefNo";
                break;
            case 4:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "Status";
                break;
            case 5:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "Item";
                break;
            case 6:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "Floor";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "GPS";
                break;
            case 8:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "Location";
                break;
            case 9:
                info.type = PropertyInfo.INTEGER_CLASS;
                info.name = "User";
                break;
            case 10:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DateLogged";
                break;
            case 11:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DateOpened";
                break;
            case 12:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DateClosed";
                break;
            default:
                break;
        }
    }
}
