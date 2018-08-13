package com.batsamayi.ndincedetu;

import android.util.Base64;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

public class Users implements KvmSerializable {
    public int Id;
    public String Username;
    public String Password;
    public String FirstName;
    public String LastName;
    public String DateModified;
    public String StudentNo;
    public String UserType;
    public String Verifier;
    public boolean IsApproved;
    public String DeviceToken;
    public byte[] Picture;

    Users() { }

    @Override
    public Object getProperty(int index) {
        switch (index) {
            case 0: return Id;
            case 1: return Username;
            case 2: return Password;
            case 3: return FirstName;
            case 4: return LastName;
            case 5: return DateModified;
            case 6: return StudentNo;
            case 7: return UserType;
            case 8: return Verifier;
            case 9: return IsApproved;
            case 10:
                return DeviceToken;
            case 11:
                return Picture;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 12;
    }

    @Override
    public void setProperty(int index, Object value) {
        switch (index) {
            case 0: Id = Integer.parseInt(value.toString()); break;
            case 1: Username = value.toString(); break;
            case 2: Password = value.toString(); break;
            case 3: FirstName = value.toString(); break;
            case 4: LastName = value.toString(); break;
            case 5: DateModified = value.toString(); break;
            case 6: StudentNo = value.toString(); break;
            case 7: UserType = value.toString(); break;
            case 8: Verifier = value.toString(); break;
            case 9: IsApproved = Boolean.parseBoolean(value.toString()); break;
            case 10:
                DeviceToken = value.toString();
                break;
            case 11:
                Picture = Base64.decode(value.toString(), Base64.DEFAULT);
                break;
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
                info.name = "Username";
                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Password";
            break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "FirstName";
            break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "LastName";
                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DateModified";
                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "StudentNo";
                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "UserType";
                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Verifier";
                break;
            case 9:
                info.type = PropertyInfo.BOOLEAN_CLASS;
                info.name = "IsApproved";
                break;
            case 10:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "DeviceToken";
                break;
            case 11:
                info.type = MarshalBase64.BYTE_ARRAY_CLASS;
                info.name = "Picture";
                break;
            default:
                break;
        }
    }
}
