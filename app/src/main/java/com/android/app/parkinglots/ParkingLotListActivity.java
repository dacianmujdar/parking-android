package com.android.app.parkinglots;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.app.parkinglots.dummy.OAuth;
import com.android.app.parkinglots.dummy.Parking;
import com.android.app.parkinglots.dummy.Request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * An activity representing a list of Requests. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ParkingLotDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 *
 *
 * Master detail flow when the app was created.
 * Model
 * List -> type: RecyclerView, containing cells
 * The whole list is registered for a context menu
 * The Adapter links the model with the view
 */
public class ParkingLotListActivity extends AppCompatActivity {

    private Parking data;
    private ArrayList<Request> requests;
    private SimpleItemRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parkinglot_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        FloatingActionButton emailInput = (FloatingActionButton) findViewById(R.id.fab);
        emailInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start the email input activity
                Context context = view.getContext();
                Intent intent = new Intent(context, CreateRequestActivity.class);
                context.startActivity(intent);
            }
        });
        View recyclerView = findViewById(R.id.parkinglot_list);
        assert recyclerView != null;
        // here the whole parking info is fetched from local storage
        data = Parking.getInstance(this);
        // get only the requests data
        requests = data.getRequests();
        // get the parking data from the server and override it.
        // If the data isn't fetched successfully, then the view displays the data that was fetched from local storage.
        getDataFromAPI();
        setupRecyclerView((RecyclerView) recyclerView);
        registerForContextMenu((RecyclerView) recyclerView);
    }


    /**
     * Get the parking data from the server.
     */
    private void getDataFromAPI() {
        RequestQueue queue = Volley.newRequestQueue(this);
        // here the parking info is fetched from the server
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, Network.URL + "parking/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Log.d("logr=", response);
                        Parking.updateDataAfterAPIRequest(response);
                        if (Parking.getInstance(ParkingLotListActivity.this).get_is_administrator()) {
                            Toast.makeText(ParkingLotListActivity.this, "Welcome! You have administrator rights!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ParkingLotListActivity.this, "Welcome! You don't have administrator rights so you can access all features!", Toast.LENGTH_LONG).show();
                        }
                        refreshDataOnAdapter();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                ;
                // add headers <key,value>
                headers.put("Authorization", "Bearer " + OAuth.get_token());
                return headers;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    // refresh adapter when new data is available
    private void refreshDataOnAdapter() {
        data = null;
        requests.clear();
        adapter.notifyDataSetChanged();
        data = Parking.getInstance(ParkingLotListActivity.this);
        requests.addAll(data.getRequests());
        adapter.notifyDataSetChanged();
    }

    private void removeRequest(final Request request) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest dr = new StringRequest(com.android.volley.Request.Method.DELETE, Network.URL + "/requests/" + request
                .getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        data.removeRequest(request);
                        refreshDataOnAdapter();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                ;
                // add headers <key,value>
                headers.put("Authorization", "Bearer " + OAuth.get_token());
                return headers;
            }
        };
        queue.add(dr);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenu.ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater m = getMenuInflater();
        m.inflate(R.menu.delete_request, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_item:
                Request request = adapter.getCurrentItem();
                if (request != null) {
                    removeRequest(request);
                }
                this.adapter.notifyDataSetChanged();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshDataOnAdapter();
    }

    // the data is saved into local storage on pause
    @Override
    protected void onPause() {
        super.onPause();
        Parking.storeDataIntoPersistance(this);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleItemRecyclerViewAdapter(this, requests, false);
        recyclerView.setAdapter(adapter);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ParkingLotListActivity mParentActivity;
        private final List<Request> mValues;
        private Request mCurrentItem;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Request item = (Request) view.getTag();
                Context context = view.getContext();
                Intent intent = new Intent(context, ParkingLotDetailActivity.class);
                intent.putExtra(ParkingLotDetailActivity.ARG_ITEM_ID, item.getId());
                context.startActivity(intent);
            }
        };

        public Request getCurrentItem() {
            return mCurrentItem;
        }

        SimpleItemRecyclerViewAdapter(ParkingLotListActivity parent,
                List<Request> items,
                boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.parkinglot_list_content, parent, false);
            return new ViewHolder(view);
        }

        // this is the concrete adapter, where the model is linked with the UI
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mTypeView.setText(mValues.get(position).getType());
            holder.mStatusView.setText(mValues.get(position).getRentalRequestedAt());
            holder.mCreatedByView.setText(mValues.get(position).getCreatedBy());
            holder.mRequestedForView.setText(mValues.get(position).getRequestedFor());
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
            //holder.itemView.setOnLongClickListener(mOnLongClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        // this represents a cell in the list
        class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

            final TextView mTypeView;
            final TextView mStatusView;
            final TextView mCreatedByView;
            final TextView mRequestedForView;

            ViewHolder(View view) {
                super(view);
                mTypeView = (TextView) view.findViewById(R.id.type);
                mStatusView = (TextView) view.findViewById(R.id.status);
                mCreatedByView = (TextView) view.findViewById(R.id.createdby);
                mRequestedForView = (TextView) view.findViewById(R.id.createdfor);
                view.setOnCreateContextMenuListener(this);
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                    ContextMenu.ContextMenuInfo menuInfo) {
                mCurrentItem = (Request) v.getTag();
            }
        }
    }
}
