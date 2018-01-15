package com.android.app.parkinglots;

import com.google.gson.Gson;

import com.android.app.parkinglots.dummy.OAuth;
import com.android.app.parkinglots.dummy.Parking;
import com.android.app.parkinglots.dummy.RequestType;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class CreateRequestActivity extends AppCompatActivity {

    EditText mEmailET;
    EditText mCreatorNameET;
    EditText mReceiverNameET;
    Spinner mTypeS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_form);
        Button mSendEmail = (Button) findViewById(R.id.email_send_email_button);
        mSendEmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                createRequestOnAPI();
            }
        });
        mEmailET = (EditText) findViewById(R.id.email);
        mCreatorNameET = (EditText) findViewById(R.id.creator_name);
        mReceiverNameET = (EditText) findViewById(R.id.receiver_name);
        mTypeS = (Spinner) findViewById(R.id.request_types);
        // dropdown list containing the request types
        setRequestTypeSpinner();
    }

    private void createRequestOnAPI() {
        final String creator_name = mCreatorNameET.getText().toString();
        final String receiver_name = mReceiverNameET.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest postRequest = new StringRequest(Request.Method.POST, Network.URL + "requests/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        Gson gson = new Gson();
                        com.android.app.parkinglots.dummy.Request request = gson.fromJson(response, com.android.app.parkinglots.dummy.Request.class);
                        sendEmailTask(request);
                        // add request to local storage as well
                        Parking.getInstance(CreateRequestActivity.this).addNewRequest(request);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (mTypeS.getSelectedItem() != null) {
                    params.put("type", mTypeS.getSelectedItem().toString());
                } else {
                    params.put("type", Constants.TYPE_DEFAULT);
                }
                params.put("requestedAt", Constants.REQUESTED_AT_DEFAULT);
                params.put("period", Constants.PERIOD_DEFAULT);
                params.put("requestedFor", receiver_name);
                params.put("createdBy", creator_name);
                params.put("requestedFrom", Constants.REQUESTED_FROM_DEFAULT);
                params.put("reservationRequestedAt", Constants.RESERVATION_REQUESTED_AT_DEFAULT);
                params.put("rentalRequestedAt", Constants.RENTAL_REQUESTED_AT_DEFAULT);
                params.put("parkingNo", Constants.PARKING_NO_DEFAULT);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                ;
                // add headers <key,value>
                headers.put("Authorization", "Bearer " + OAuth.get_token());
                return headers;
            }
        };
        queue.add(postRequest);
    }

    private void setRequestTypeSpinner() {
        ArrayList<RequestType> requestTypes = Parking.getInstance(this).getRequestTypes();
        ArrayAdapter<RequestType> spinnerAdapter = new ArrayAdapter<RequestType>(this, android.R.layout.simple_spinner_dropdown_item, requestTypes);
        mTypeS.setAdapter(spinnerAdapter);
    }

    private void sendEmailTask(final com.android.app.parkinglots.dummy.Request request) {
        final String email = mEmailET.getText().toString();
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(CreateRequestActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(CreateRequestActivity.this);
        }
        builder.setTitle("Create request")
                .setMessage("Do you want to email this request?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // send email( intent)
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                        i.putExtra(Intent.EXTRA_SUBJECT, request.getCreatedBy() + " invited you to park!"); // move this to constants
                        // TO DO - build the actual body of the email
                        i.putExtra(Intent.EXTRA_TEXT, "Hello, " + request.getRequestedFor() + "!");
                        try {
                            startActivity(Intent.createChooser(i, "Send mail..."));
                            Toast.makeText(CreateRequestActivity.this, "Request sent", Toast.LENGTH_SHORT).show();
                        } catch (android.content.ActivityNotFoundException ex) {
                            //quick little message for the user
                            Toast.makeText(CreateRequestActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        Toast.makeText(CreateRequestActivity.this, "Request created", Toast.LENGTH_SHORT).show();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}

