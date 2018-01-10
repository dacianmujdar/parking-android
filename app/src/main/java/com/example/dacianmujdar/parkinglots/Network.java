package com.example.dacianmujdar.parkinglots;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dacianmujdar.parkinglots.dummy.Parking;

import android.app.Activity;
/**
 * Created by dacianmujdar on 1/10/18.
 */
public class Network {

    public static final String URL = "https://parking-django.herokuapp.com/";

    // returns a parking object optain from the API
    public static Parking getDataFromAPI(Activity activity) {
        RequestQueue queue = Volley.newRequestQueue(activity);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL + "parking/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        response = response;
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error = error;
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
        return null;
    }
}
