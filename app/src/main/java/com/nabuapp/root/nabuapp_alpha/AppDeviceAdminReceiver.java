package com.nabuapp.root.nabuapp_alpha;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by ROOT on 13.06.2016.
 */
public class AppDeviceAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
    }

}
