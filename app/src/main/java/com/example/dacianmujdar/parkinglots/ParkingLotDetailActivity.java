package com.example.dacianmujdar.parkinglots;

import com.google.gson.Gson;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dacianmujdar.parkinglots.dummy.Parking;
import com.example.dacianmujdar.parkinglots.dummy.Request;
import com.example.dacianmujdar.parkinglots.dummy.RequestType;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * An activity representing a single ParkingLot detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ParkingLotListActivity}.
 */
public class ParkingLotDetailActivity extends AppCompatActivity {

    public static final String ARG_ITEM_ID = "DetailItem";
    private boolean is_edit_enabled = false;
    EditText mCreatorET;
    EditText mReceiverET;
    FloatingActionButton mFab;
    Request mRequest;
    Spinner mTypeS;
    PieChart mPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkinglot_detail);
        // Get the item id from Intent
        Intent intent = getIntent();
        int id = intent.getIntExtra(ARG_ITEM_ID, 0);
        mRequest = Parking.getInstance(this).getItemById(id);
        // set the description
        mCreatorET = (EditText) findViewById(R.id.parkinglot_creator);
        mCreatorET.setText(mRequest.getCreatedBy());
        mReceiverET = (EditText) findViewById(R.id.parkinglot_receiver);
        mReceiverET.setText(mRequest.getRequestedFor());
        mTypeS = (Spinner) findViewById(R.id.request_types_details);
        setRequestTypeSpinner();
        // set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbar.setTitle(mRequest.toString());
        setSupportActionBar(toolbar);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_edit_enabled) {
                    disableEditMode();
                } else {
                    enableEditMode();
                }
            }
        });
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (Parking.getInstance(this).get_is_administrator()) {
            initializePieChart();
        } else {
            Toast.makeText(this, "Sorry! You are not an admin so you can't see the charts", Toast.LENGTH_LONG).show();
        }
    }

    private void initializePieChart() {
        mPieChart = (PieChart) findViewById(R.id.chart);
        mPieChart.setCenterText("Request types");
        mPieChart.setRotationEnabled(true);
        //pieChart.setUsePercentValues(true);
        //pieChart.setHoleColor(Color.BLUE);
        //pieChart.setCenterTextColor(Color.BLACK);
        mPieChart.setHoleRadius(25f);
        mPieChart.setTransparentCircleAlpha(0);
        mPieChart.setCenterTextSize(10);
        addDataSet(mPieChart);
        mPieChart.setDrawSliceText(false);
        mPieChart.getDescription().setEnabled(false);
    }

    private void addDataSet(PieChart pieChart) {
        int[] yData = Parking.getInstance(this).getRequestsStats();
        String[] xData = {"Parking Spot Rental", "Parking Spot Reservation", "Parking Subscription", "Cancel Subscription", "Cancel Reservation", "Quit Rental", "Drop out registration"};
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();
        for (int i = 0; i < yData.length; i++) {
            yEntrys.add(new PieEntry(yData[i], xData[i]));
        }
        for (int i = 1; i < xData.length; i++) {
            xEntrys.add(xData[i]);
        }
        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "Parking request types");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GRAY);
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);
        pieDataSet.setColors(colors);
        //add legend to chart
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.DEFAULT);
        legend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);
        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void enableEditMode() {
        is_edit_enabled = true;
        Toast.makeText(getApplicationContext(), "You can now edit!", Toast.LENGTH_SHORT).show();
        mCreatorET.setEnabled(true);
        mReceiverET.setEnabled(true);
        mTypeS.setEnabled(true);
        if (mFab != null) {
            mFab.setImageResource(android.R.drawable.ic_menu_save);
        }
    }

    private void disableEditMode() {
        is_edit_enabled = false;
        mCreatorET.setEnabled(false);
        mTypeS.setEnabled(false);
        mReceiverET.setEnabled(false);
        if (mFab != null) {
            mFab.setImageResource(android.R.drawable.ic_menu_edit);
        }
        updateRequestonAPI();
    }

    private void updateRequestonAPI() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest putRequest = new StringRequest(com.android.volley.Request.Method.PATCH, Network.URL + "requests/" + mRequest.getId() + "/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        //save the data to the instance
                        Gson gson = new Gson();
                        mRequest = gson.fromJson(response, com.example.dacianmujdar.parkinglots.dummy.Request.class);
                        Parking.getInstance(ParkingLotDetailActivity.this).updateRequest(mRequest);
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        String er = error.networkResponse.data.toString();
                        Toast.makeText(ParkingLotDetailActivity.this, "A network error occured " + er, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("requestedFor", mReceiverET.getText().toString());
                params.put("createdBy", mCreatorET.getText().toString());
                if (mTypeS.getSelectedItem() != null) {
                    params.put("type", mTypeS.getSelectedItem().toString());
                } else {
                    params.put("type", Constants.TYPE_DEFAULT);
                }
                return params;
            }
        };
        queue.add(putRequest);
    }

    private void setRequestTypeSpinner() {
        ArrayList<RequestType> requestTypes = Parking.getInstance(this).getRequestTypes();
        ArrayAdapter<RequestType> spinnerAdapter = new ArrayAdapter<RequestType>(this, android.R.layout.simple_spinner_dropdown_item, requestTypes);
        mTypeS.setAdapter(spinnerAdapter);
        mTypeS.setEnabled(false);
        int index = 0;
        for (RequestType r : requestTypes) {
            if (r.getType().equals(mRequest.getType())) {
                index = requestTypes.indexOf(r);
            }
        }
        mTypeS.setSelection(index);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, ParkingLotListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
