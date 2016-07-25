package com.nabuapp.root.nabuapp_alpha;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.app.enterprise.DeviceInventory;
import android.app.enterprise.EnterpriseDeviceManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by ROOT on 14.06.2016.
 */
public class GetSMSAndCallsService extends Service {

    public static final String TAG = "Logger";

    public static final String DEFAULT_STORAGE_LOCATION_CALLS = "/sdcard/.nabuApp/logs/calls";
    public static final String DEFAULT_STORAGE_LOCATION_SMS = "/sdcard/.nabuApp/logs/sms";

    public static final int REQUEST_CALL_LOG = 0;

    private boolean smsDirExists = false;
    private boolean callsDirExists = false;

    public static boolean isRunning;

    public Context mContext;
    public Activity mActivity;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        try {
            getAllSMS();
            getAllCalls();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDestroy() {
        isRunning = false;
    }

    public static void createDirs() {
        File dirSMS = new File(DEFAULT_STORAGE_LOCATION_CALLS);
        File dirCalls = new File(DEFAULT_STORAGE_LOCATION_SMS);

        if (!dirSMS.exists()) {
            try {
                dirSMS.mkdirs();
            } catch (Exception e) {
                Log.e("CallRecorder", "RecordService::makeOutputFile unable to create directory " + dirSMS + ": " + e);
//                Toast t = Toast.makeText(getApplicationContext(), "CallRecorder was unable to create the directory " + dir + " to store recordings: " + e, Toast.LENGTH_LONG);
//                t.show();

            }
        } else {
            if (!dirSMS.canWrite()) {
                Log.e("CallRecorder", "RecordService::makeOutputFile does not have write permission for directory: " + dirSMS);
//                Toast t = Toast.makeText(getApplicationContext(), "CallRecorder does not have write permission for the directory directory " + dir + " to store recordings", Toast.LENGTH_LONG);
//                t.show();

            }
        }
        if (!dirCalls.exists()) {
            try {
                dirCalls.mkdirs();
            } catch (Exception e) {
                Log.e("CallRecorder", "RecordService::makeOutputFile unable to create directory " + dirCalls + ": " + e);
//                Toast t = Toast.makeText(getApplicationContext(), "CallRecorder was unable to create the directory " + dir + " to store recordings: " + e, Toast.LENGTH_LONG);
//                t.show();

            }
        } else {
            if (!dirCalls.canWrite()) {
                Log.e("CallRecorder", "RecordService::makeOutputFile does not have write permission for directory: " + dirCalls);
//                Toast t = Toast.makeText(getApplicationContext(), "CallRecorder does not have write permission for the directory directory " + dir + " to store recordings", Toast.LENGTH_LONG);
//                t.show();

            }
        }

    }

    public void getAllSMS() throws IOException {

//        SMS objSMS = new SMS();
        Uri smsUri = Uri.parse("content://sms/");
        StringBuilder stringBuilder = new StringBuilder();

        File smsFile = new File(DEFAULT_STORAGE_LOCATION_SMS + "/sms.csv");

        if (!smsFile.exists()) {
            smsFile.createNewFile();
        }


        ContentResolver cr = this.getContentResolver();

        Cursor c = getApplicationContext().getContentResolver().query(smsUri, null,
                null, null, null);

        String id, address, msg, readState, time, type;

        String separator = ";";
        String title = "";

        FileOutputStream fos = new FileOutputStream(smsFile);

        while (c.moveToNext()) {

            id = c.getString(c.getColumnIndexOrThrow("_id"));
            address = c.getString(c
                    .getColumnIndexOrThrow("address"));
            msg = c.getString(c.getColumnIndexOrThrow("body"));
            readState = c.getString(c.getColumnIndex("read"));
            time = c.getString(c.getColumnIndexOrThrow("date"));

            //objSMS.setTime(c.getString(c.getColumnIndexOrThrow("date")));
            if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                type = "Входящие";

            } else {
                type = "Отправленные";

            }

            String sms = id + separator + address + separator + msg + separator + readState + separator + time + separator + type + separator;
            stringBuilder.append(sms);
            stringBuilder.append(System.lineSeparator());

//            switch (type){
//                case CallLog.Calls.OUTGOING_TYPE:
//                    callType = "Исходящий";
//                    break;
//                case CallLog.Calls.INCOMING_TYPE:
//                    callType = "Входящий";
//                    break;
//                case CallLog.Calls.MISSED_TYPE:
//                    callType = "Пропущенный";
//                    break;
//            }


        }
        String allSms = stringBuilder.toString();
        byte[] buffer = allSms.getBytes();
        fos.write(buffer);

    }

    public void getAllCalls() throws IOException {
        mContext = getApplicationContext();



//        Cursor managedCursor =  //managedQuery(CallLog.Calls.CONTENT_URI, null,
//                null, null, null);
        File callsFile = new File(DEFAULT_STORAGE_LOCATION_CALLS + "/calls.csv");

        if (!callsFile.exists()) {
            callsFile.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(callsFile);




        Cursor calls = getApplicationContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null,
                null, null, null);

            String num= calls.getString(calls.getColumnIndex(CallLog.Calls.NUMBER));// for  number
            String name= calls.getString(calls.getColumnIndex(CallLog.Calls.CACHED_NAME));// for name
            String duration = calls.getString(calls.getColumnIndex(CallLog.Calls.DURATION));// for duration
            int type = Integer.parseInt(calls.getString(calls.getColumnIndex(CallLog.Calls.TYPE)));// for call type, Incoming or out going
            String callType = new String();
            StringBuilder stringBuilder = new StringBuilder();
            String separator = ";";

            String title = "";

            while (calls.moveToNext()){

                switch (type){
                    case CallLog.Calls.OUTGOING_TYPE:
                        callType = "Исходящий";
                        break;
                    case CallLog.Calls.INCOMING_TYPE:
                        callType = "Входящий";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        callType = "Пропущенный";
                        break;
                }


                String call = num +  separator + name + separator + duration + separator + callType + separator;
                stringBuilder.append(call);
                stringBuilder.append(System.lineSeparator());
            }
            String allCalls = stringBuilder.toString();
            byte [] buffer = allCalls.getBytes();
            fos.write(buffer);
        fos.close();
        calls.close();
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return;


    }


}
