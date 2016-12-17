package com.example.flerchy.codeforcesclient;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class WifiStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nw = connMgr.getActiveNetworkInfo();

        boolean isConnected = nw != null && nw.isConnectedOrConnecting();
        String status;
        if (isConnected) {
            status = context.getString(R.string.connected_status);
        } else {
            status = context.getString(R.string.warning);
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_icon)
                        .setContentTitle(context.getString(R.string.note_title))
                        .setContentText(status);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

    }
}