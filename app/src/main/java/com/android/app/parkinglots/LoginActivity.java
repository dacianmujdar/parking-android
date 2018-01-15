package com.android.app.parkinglots;

import com.google.gson.Gson;

import com.android.app.parkinglots.dummy.OAuth;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements Observer {

    private static Map<String, String> users;
    private EditText mEmailET;
    private EditText mPasswordET;
    private Button mLogInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailET = (EditText) findViewById(R.id.email_et);
        mPasswordET = (EditText) findViewById(R.id.password_et);
        mLogInButton = (Button) findViewById(R.id.log_in_button);
        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performLogIn(view);
            }
        });
        // start the network observer
        NetworkObserver runnable = NetworkObserver.getInstance(this);
        runnable.registerObserver(this);
        new Thread(runnable).start();
    }

    // Volley library for networking
    // Listener( callback - when the request ended) - String awaited
    // ErrorListener( server error)
    // queue -> async requests queue
    private void performLogIn(final View view) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest putRequest = new StringRequest(com.android.volley.Request.Method.POST, NetworkObserver.URL + "oauth2/token/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //save the data to the instance
                        Gson gson = new Gson();
                        OAuth auth_info = gson.fromJson(response, com.android.app.parkinglots.dummy.OAuth.class);
                        OAuth.set_token(auth_info.getAccessToken());
                        Log.d("Response", response);
                        // go to main activity
                        Context context = view.getContext();
                        Intent intent = new Intent(context, ParkingLotListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        finish(); // call this to finish the current activity
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        String er = error.networkResponse.data.toString();
                        Toast.makeText(LoginActivity.this, "Invalid credentials " + er, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", mEmailET.getText().toString());
                params.put("password", mPasswordET.getText().toString());
                params.put("client_id", "jZeRQs68ZvlXAe4x5VCYVw0exvq3Pclu21wztAnY");
                params.put("client_secret", "KR6zeaGvjEZwbPndcUNRZS5XmTGWpie2XCyBbfG4nljYiCk5VPXh6phbYFX6zhke6Rq33QPngby4SgyBgX943zsNQAmDZHzhZ1YAR2r2KKSuhcDniow8vpVr4pSOXzx8");
                params.put("grant_type", "password");
                return params;
            }
        };
        queue.add(putRequest);
    }

    private boolean validCredentials(String email, String pass) {
        return users.containsKey(email) && users.get(email).equals(pass);
    }

    @Override
    public void update(final boolean is_connected) {
        LoginActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                if (is_connected) {
                    LoginActivity.this.handleOnlineMode();
                } else {
                    LoginActivity.this.handleOfflineMode();
                }
            }
        });
    }

    private void handleOfflineMode() {
        Toast.makeText(LoginActivity.this, "You are offline! Check internet connection and come back!", Toast.LENGTH_SHORT).show();
        mLogInButton.setEnabled(false);
        mEmailET.setEnabled(false);
        mPasswordET.setEnabled(false);
    }

    private void handleOnlineMode() {
        //Toast.makeText(LoginActivity.this, "You are back online!", Toast.LENGTH_SHORT).show();
        mLogInButton.setEnabled(true);
        mEmailET.setEnabled(true);
        mPasswordET.setEnabled(true);
    }
}