package com.example.dacianmujdar.parkinglots;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class InputFormActivity extends AppCompatActivity {

    EditText mEmailET;
    EditText mFirstNameET;
    EditText mLastNameET;
    EditText mMessageET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_form);
        Button mSendEmail = (Button) findViewById(R.id.email_send_email_button);
        mSendEmail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmailTask();
            }
        });
        mEmailET = (EditText) findViewById(R.id.email);
        mFirstNameET = (EditText) findViewById(R.id.first_name);
        mLastNameET = (EditText) findViewById(R.id.last_name);
        mMessageET = (EditText) findViewById(R.id.message);
    }

    private void sendEmailTask() {
        String email = mEmailET.getText().toString();
        String first_name = mFirstNameET.getText().toString();
        String last_name = mLastNameET.getText().toString();
        String message = mMessageET.getText().toString();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        i.putExtra(Intent.EXTRA_SUBJECT, "Message from Parking App"); // move this to constants
        // TO DO - build the actual body of the email
        i.putExtra(Intent.EXTRA_TEXT, "Hello, " + first_name + " " + last_name + "!");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(InputFormActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}

