package com.nabuapp.root.nabuapp_alpha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

/**
 * Created by ROOT on 13.06.2016.
 */
public class PhoneReceiver extends BroadcastReceiver {

    private String phoneNumber;

    @Override
    public void onReceive(Context context, Intent intent) {
        phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        String extraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        try {
//            SharedPreferences settings = context.getSharedPreferences(
//                    CallRecorderConstants.LISTEN_ENABLED, 0);
//            boolean silent = settings.getBoolean("silentMode", true);
            if (extraState != null) {
                if (extraState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    Intent myIntent = new Intent(context,
                            CallRecorderService.class);
                    myIntent.putExtra("commandType",
                            CallRecorderConstants.STATE_CALL_START);
                    context.startService(myIntent);
                } else if (extraState
                        .equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    Intent myIntent = new Intent(context,
                            CallRecorderService.class);
                    myIntent.putExtra("commandType",
                            CallRecorderConstants.STATE_CALL_END);
                    context.startService(myIntent);
                } else if (extraState
                        .equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    if (phoneNumber == null)
                        phoneNumber = intent
                                .getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Intent myIntent = new Intent(context,
                            CallRecorderService.class);
                    myIntent.putExtra("commandType",
                            CallRecorderConstants.STATE_INCOMING_NUMBER);
                    myIntent.putExtra("phoneNumber", phoneNumber);
//                    myIntent.putExtra("silentMode", silent);
                    context.startService(myIntent);
                }
            }
            else if (phoneNumber != null) {
                Intent myIntent = new Intent(context, CallRecorderService.class);
                myIntent.putExtra("commandType",
                        CallRecorderConstants.STATE_INCOMING_NUMBER);
                myIntent.putExtra("phoneNumber", phoneNumber);
//                myIntent.putExtra("silentMode", silent);
                context.startService(myIntent);
            }

        }
        catch (Exception e){

        }


    }

}
