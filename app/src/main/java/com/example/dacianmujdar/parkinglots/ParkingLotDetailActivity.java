package com.example.dacianmujdar.parkinglots;

import com.example.dacianmujdar.parkinglots.dummy.ParkingLot;
import com.example.dacianmujdar.parkinglots.dummy.ParkingLotCollection;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
/**
 * An activity representing a single ParkingLot detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ParkingLotListActivity}.
 */
public class ParkingLotDetailActivity extends AppCompatActivity {

    public static final String ARG_ITEM_ID = "DetailItem";
    private boolean is_edit_enabled = false;
    EditText mDetailsET;
    FloatingActionButton mFab;
    ParkingLot mParkingLot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkinglot_detail);
        // Get the item id from Intent
        Intent intent = getIntent();
        int id = intent.getIntExtra(ARG_ITEM_ID, 0);
        mParkingLot = ParkingLotCollection.getItemById(id);
        // set the description
        mDetailsET = (EditText) findViewById(R.id.parkinglot_detail);
        mDetailsET.setText(mParkingLot.description);
        // set the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        toolbar.setTitle(mParkingLot.name);
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
    }

    private void enableEditMode() {
        is_edit_enabled = true;
        Toast.makeText(getApplicationContext(), "You can now edit!", Toast.LENGTH_SHORT).show();
        mDetailsET.setEnabled(true);
        if (mFab != null) {
            mFab.setImageResource(android.R.drawable.ic_menu_save);
        }
    }

    private void disableEditMode() {
        is_edit_enabled = false;
        mDetailsET.setEnabled(false);
        if (mFab != null) {
            mFab.setImageResource(android.R.drawable.ic_menu_edit);
        }
        //save the data to the instance
        mParkingLot.description = mDetailsET.getText().toString();
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
