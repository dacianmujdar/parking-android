package com.example.dacianmujdar.parkinglots;

import com.example.dacianmujdar.parkinglots.dummy.Parking;
import com.example.dacianmujdar.parkinglots.dummy.Request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
/**
 * An activity representing a list of ParkingLots. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ParkingLotDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ParkingLotListActivity extends AppCompatActivity {

    private Parking data;

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
                Intent intent = new Intent(context, InputFormActivity.class);
                context.startActivity(intent);
            }
        });
        View recyclerView = findViewById(R.id.parkinglot_list);
        assert recyclerView != null;
        data = Parking.loadDataFromJson(this);
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, data.getRequests(), false));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ParkingLotListActivity mParentActivity;
        private final List<Request> mValues;
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

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(Integer.toString(mValues.get(position).getId()));
            holder.mContentView.setText(mValues.get(position).getCreatedBy());
            holder.mNameView.setText(mValues.get(position).getStatus());
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            final TextView mIdView;
            final TextView mContentView;
            final TextView mNameView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mNameView = (TextView) view.findViewById(R.id.name);
                mContentView = (TextView) view.findViewById(R.id.description);
            }
        }
    }
}
