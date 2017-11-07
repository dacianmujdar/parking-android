package com.example.dacianmujdar.parkinglots.dummy;

import com.google.gson.Gson;

import android.app.Activity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
public class Parking {

    private ArrayList<String> status;

    public ArrayList<String> getStatus() {
        return this.status;
    }

    public void setStatus(ArrayList<String> status) {
        this.status = status;
    }

    private ArrayList<RequestType> requestTypes;

    public ArrayList<RequestType> getRequestTypes() {
        return this.requestTypes;
    }

    public void setRequestTypes(ArrayList<RequestType> requestTypes) {
        this.requestTypes = requestTypes;
    }

    private ArrayList<Request> requests;

    public ArrayList<Request> getRequests() {
        return this.requests;
    }

    public void setRequests(ArrayList<Request> requests) {
        this.requests = requests;
    }

    private ArrayList<User> users;

    public ArrayList<User> getUsers() {
        return this.users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public static Parking loadDataFromJson(Activity activity) {
        String json = null;
        try {
            InputStream is = activity.getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        Gson gson = new Gson();
        Parking parking = gson.fromJson(json, Parking.class);
        return parking;
    }
}