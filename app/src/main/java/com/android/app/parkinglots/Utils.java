package com.android.app.parkinglots;

import android.content.Context;
import android.net.ConnectivityManager;

public class Utils {

    // Simply checks if an internet connection is available
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
