package com.nabuapp.root.nabuapp_alpha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

@SuppressWarnings("unused")
public class CommandReceiver extends BroadcastReceiver {

    static  Intent startRecIntent = new Intent(AppActions.VOICE_REC_START_ACTION);
    static  Intent stopRecIntent = new Intent(AppActions.VOICE_REC_STOP_ACTION);



    public CommandReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        if (startRecIntent.equals(intent.getAction())  ) {
            context.startService(startRecIntent);
        }
        if (stopRecIntent.equals(intent.getAction())){
            context.stopService(stopRecIntent);
        }
    }
}
