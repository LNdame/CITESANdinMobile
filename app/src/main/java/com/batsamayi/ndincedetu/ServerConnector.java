package com.batsamayi.ndincedetu;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.SparseArray;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.concurrent.Executors;

class ServerConnector {
    private final String WSDL_TARGET_NAMESPACE = "http://tu.ufh.ac.za/";
    private final String SOAP_ADDRESS = "http://cite.dedicated.co.za/androidservices.asmx";

    static final String ERROR_RESOLVE_HOST = "Error: Unable to reach to server.\n" +
            "Please ensure that you are connected to the internet, then try again";
    static final String UNKNOWN_ERROR = "Unknown Error. Please contact the developer if this issue persists.";
    static final String AUTHENTICATION_FAILED = "Error: Authentication failed. Please check your internet connection";

    public ServerConnector() { }

    public String userCreate(Users user) {
        return callWebMethod("CreateUser", user).toString();
    }

    public String userModify(Users user) {
        return callWebMethod("RenameUser", user).toString();
    }

    public String userVerify(Users user) {
        return callWebMethod("VerifyUser", user).toString();
    }
    public void verifierSend(Users user) { callWebMethod("SendVerifier", user); }

    public String changePassword(Users user) {
        return callWebMethod("ChangePassword", user).toString();
    }

    public String uploadAvatar(Users user) {
        return callWebMethod("UploadAvatar", user).toString();
    }

    public Bitmap getAvatar(Users user) {
        SoapObject response = (SoapObject) callWebMethod("GetAvatar", user);
        byte[] byteArray = Base64.decode(response.getProperty(10).toString(), Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    static String parseError(Object object) {
        try {
            String error = object.toString().toLowerCase();
            if (error.contains("unable to") || error.contains("failed")) {
                if (error.contains("resolve host"))
                    return ERROR_RESOLVE_HOST;
                else if (error.contains("authentication"))
                    return AUTHENTICATION_FAILED;
                else return UNKNOWN_ERROR;
            } else return null;
        } catch (Exception ex) {
            return UNKNOWN_ERROR;
        }
    }
    public Users userLogin(Users user) {
        SoapObject response = (SoapObject) callWebMethod("UserLogin", user);
        try {
            user.Id = Integer.parseInt(response.getProperty(0).toString());
            user.Username = response.getProperty(1).toString();
            user.Password = response.getProperty(2).toString();
            user.FirstName = response.getProperty(3).toString();
            user.LastName = response.getProperty(4).toString();
            user.DateModified = response.getProperty(5).toString();
            user.StudentNo = response.getProperty(6).toString();
            user.UserType = response.getProperty(7).toString();
            user.Verifier = response.getProperty(8).toString();
            user.IsApproved = Boolean.valueOf(response.getProperty(9).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public String logFault(Fault fault, byte[] picture) {
        if (fault == null) return "NULL!";
        fault.Picture = picture;
        String OPERATION_NAME = "LogFault";
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("fault");
        pi.setValue(fault);
        pi.setType(fault.getClass());
        request.addProperty(pi);
        request.addProperty("picture", picture);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        envelope.addMapping(WSDL_TARGET_NAMESPACE, "Fault", fault.getClass());
        new MarshalBase64().register(envelope);
        envelope.encodingStyle = SoapEnvelope.ENC;

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        try {
            httpTransport.call(WSDL_TARGET_NAMESPACE+OPERATION_NAME, envelope);
            Object response = envelope.getResponse();
            return response.toString();
        } catch(XmlPullParserException | IOException e) {
            return e.getMessage();
        }
    }

    public void getFaultCategories() {
        try {
            FaultCategory.fromConcat(callWebMethod("GetFaultCategories"));
        } catch(Exception e) {
            FaultCategory.knownSpinnables = new SparseArray<>(1);
            FaultCategory error = new FaultCategory();
            error.Description = e.getMessage();
            FaultCategory.knownSpinnables.put(error.Id, error);
        }
    }

    public void getFaultItems() {
        FaultItem.knownSpinnables = null;
        try {
            FaultItem.fromConcat(callWebMethod("GetFaultItems"));
        } catch(Exception e) {
            FaultItem.knownSpinnables = new SparseArray<>(1);
            FaultItem error = new FaultItem();
            error.Description = e.getMessage();
            FaultItem.knownSpinnables.put(error.Id, error);
        }
    }
    public void getFaultLocations() {
        FaultLocation.knownSpinnables = null;
        try {
            FaultLocation.fromConcat(callWebMethod("GetFaultLocations"));
        } catch(Exception e) {
            FaultLocation.knownSpinnables = new SparseArray<>(1);
            FaultLocation error = new FaultLocation();
            error.Description = e.getMessage();
            FaultLocation.knownSpinnables.put(error.Id, error);
        }
    }

    public void getFaultStatuses() {
        FaultStatus.knownSpinnables = null;
        try {
            FaultStatus.fromConcat(callWebMethod("GetFaultStatuses"));
        } catch (Exception e) {
            FaultStatus.knownSpinnables = new SparseArray<>(1);
            FaultStatus error = new FaultStatus();
            error.Description = e.getMessage();
            FaultStatus.knownSpinnables.put(error.Id, error);
        }
    }

    public int[] getFaultIDs(int userID) {
        String OPERATION_NAME = "GetUserFaults";
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("userID");
        pi.setValue(userID);
        pi.setType(Integer.class);
        request.addProperty(pi);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        int[] faultList;
        try {
            httpTransport.call(WSDL_TARGET_NAMESPACE+OPERATION_NAME, envelope);
            String response = envelope.getResponse().toString();

            String[] faultID = response.split("#");
            //If no faults have been logged
            if(response.toLowerCase().contains("error"))
                return new int[]{ -1 };
            int numberOfFaults = faultID.length;
            faultList = new int[numberOfFaults];
            for(int i = 0; i < numberOfFaults; i++)
                faultList[i] = Integer.parseInt(faultID[numberOfFaults-i-1]);
            return faultList;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new int[]{ -1 };
    }

    public void userLogout(final Users user) {
        user.DeviceToken = "";
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                new ServerConnector().callWebMethod("ChangeDevice", user);
            }
        });
    }
    private Fault getFault(int faultID) {
        String OPERATION_NAME = "GetFaultByID";
        SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
        PropertyInfo pi = new PropertyInfo();
        pi.setName("faultID");
        pi.setValue(faultID);
        pi.setType(Integer.class);
        request.addProperty(pi);
        Fault fault = new Fault();

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        envelope.addMapping(WSDL_TARGET_NAMESPACE, "Fault", fault.getClass());

        HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
        try {
            httpTransport.call(WSDL_TARGET_NAMESPACE+OPERATION_NAME, envelope);
            SoapObject response = (SoapObject)envelope.getResponse();
            fault.ID = faultID;
            fault.Description = response.getProperty(1).toString();
            fault.RefNo = response.getProperty(2).toString();
            fault.Status = Integer.parseInt(response.getProperty(3).toString());
            fault.Item = Integer.parseInt(response.getProperty(4).toString());
            fault.Floor = Integer.parseInt(response.getProperty(5).toString());
            fault.GPS = response.getProperty(6).toString();
            fault.Location = Integer.parseInt(response.getProperty(7).toString());
            fault.User = Integer.parseInt(response.getProperty(8).toString());
            fault.DateLogged = response.getProperty(9).toString();
            fault.DateOpened = response.getProperty(10).toString();
            fault.DateClosed = response.getProperty(11).toString();

            return fault;
        } catch(Exception e) {
            return fault;
        }
    }

    private Object callWebMethod(String OPERATION_NAME) {
        try {
            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE,OPERATION_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
            httpTransport.call(WSDL_TARGET_NAMESPACE+OPERATION_NAME, envelope);
            return envelope.getResponse();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public SparseArray<Fault> getAllFaults(int userID) {
        int[] faultID = getFaultIDs(userID);
        SparseArray<Fault> faultArray = new SparseArray<>(faultID.length);
        try {
            //If no faults have been logged
            for (int aFaultID : faultID) {
                Fault fault = getFault(aFaultID);
                faultArray.put(fault.ID, fault);
            }
        } catch (Exception e) {
            faultArray = new SparseArray<>(1);
            Fault error = new Fault();
            error.Description = e.getMessage();
            faultArray.put(error.ID, error);
        }
        return faultArray;
    }

    private Object callWebMethod(String OPERATION_NAME, Users user) {
        try {
            SoapObject request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            if (user != null) {
                try {
                    //user.DeviceToken = FirebaseInstanceId.getInstance().getToken();
                    //user.DeviceToken = FirebaseInstanceId.getInstance().getToken("826515574082", "FCM");
                } catch (Exception ignored) {
                }
                PropertyInfo pi = new PropertyInfo();
                pi.setName("user");
                pi.setValue(user);
                pi.setType(user.getClass());
                request.addProperty(pi);

                envelope.addMapping(WSDL_TARGET_NAMESPACE, "Users", user.getClass());
                new MarshalBase64().register(envelope);
                envelope.encodingStyle = SoapEnvelope.ENC;
            }
            HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
            httpTransport.call(WSDL_TARGET_NAMESPACE+OPERATION_NAME, envelope);

            String serverReply = parseError(envelope.getResponse());
            if (serverReply != null)
                return serverReply;

            return envelope.getResponse();
        } catch (Exception e) {
            return parseError(e.getMessage());
        }
    }
}