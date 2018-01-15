package com.android.app.parkinglots;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
public class NetworkObserver implements Observable, Runnable {

    public static final String URL = "https://parking-django.herokuapp.com/";

    private NetworkObserver() {
        observerList = new ArrayList<>();
    }

    private NetworkObserver(Context context) {
        observerList = new ArrayList<>();
        mContext = context;
    }

    private static NetworkObserver mNetwork;

    public static NetworkObserver getInstance(Context context) {
        if (mNetwork == null) {
            mNetwork = new NetworkObserver(context);
        }
        return mNetwork;
    }

    private List<Observer> observerList;
    private Context mContext;

    @Override
    public void registerObserver(Observer ob) {
        observerList.add(ob);
    }

    @Override
    public void removeObserver(Observer ob) {
        observerList.remove(ob);
    }

    @Override
    public void notifyObservers() {
        // notify all observers
        for (Observer obs : observerList) {
            obs.update();
        }
    }

    @Override
    public void run() {
        while (true) {
            Log.d("NetworkStatus", "Checking Network Status");
            if (!Utils.isNetworkConnected(mContext)) {
                notifyObservers();
                Log.d("NetworkStatus", "Network Connection is established");
            } else {
                Log.d("NetworkStatus", "Not connected to network");
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
