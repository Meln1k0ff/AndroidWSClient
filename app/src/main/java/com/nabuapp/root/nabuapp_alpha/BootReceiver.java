package com.nabuapp.root.nabuapp_alpha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ROOT on 14.06.2016.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())  ) {
            Intent activityMain = new Intent(context, MainActivity.class);
            context.startActivity(activityMain);

        }





    }
}
