package com.avivz_gavriels_elyaha.dailymealrecipes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Looper;
import android.widget.Toast;
import android.os.Handler;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ConnectivityReceiver extends BroadcastReceiver {

    private static final int DELAY_MS = 2500; // 2.5 seconds delay
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable checkConnectivityRunnable;
    private static boolean previousState = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (checkConnectivityRunnable != null) {
            handler.removeCallbacks(checkConnectivityRunnable);
        }
        checkConnectivityRunnable = () -> {
            boolean isConnected = isNetworkConnected(context);
            if (isConnected != previousState || !isConnected) {
                previousState = isConnected;
                if (!isConnected) {
                    Intent localIntent = new Intent("NO_INTERNET_CONNECTION");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
                } else {
                    Toast.makeText(context, "You are online", Toast.LENGTH_LONG).show();
                    Intent localIntent = new Intent("INTERNET_CONNECTION_RESTORED");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
                }
            }
        };
        handler.postDelayed(checkConnectivityRunnable, DELAY_MS);
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network activeNetwork = cm.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities networkCapabilities = cm.getNetworkCapabilities(activeNetwork);
                return networkCapabilities != null &&
                        (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN));
            }
        }
        return false;
    }
}

