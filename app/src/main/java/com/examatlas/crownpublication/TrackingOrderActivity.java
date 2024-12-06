package com.examatlas.crownpublication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.crownpublication.Adapter.TrackingOrderAdapter;
import com.examatlas.crownpublication.Models.TrackingOrderModel;

import java.util.ArrayList;

public class TrackingOrderActivity extends AppCompatActivity {
    ArrayList<TrackingOrderModel> trackingOrderModelArrayList;
    String createdAtDate;
    RecyclerView trackingRecyclerView;
    TrackingOrderModel trackingOrderModel;
    TrackingOrderAdapter trackingOrderAdapter;
    ImageView backBtn;
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);
        backBtn = findViewById(R.id.backBtn);

        trackingRecyclerView = findViewById(R.id.recyclerView);
        trackingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        trackingOrderModelArrayList = new ArrayList<>();
        trackingOrderAdapter = new TrackingOrderAdapter(trackingOrderModelArrayList, this);
        trackingRecyclerView.setAdapter(trackingOrderAdapter);

        // The ISO 8601 format date string
        createdAtDate = getIntent().getStringExtra("createAt");

        backBtn.setOnClickListener(v -> {
            finish();
        });

        // Step 2: Create instances of TrackingOrderModel and add them to the ArrayList
        TrackingOrderModel item1 = new TrackingOrderModel("Order Placed", createdAtDate );
        TrackingOrderModel item2 = new TrackingOrderModel("Shipped", "---");
        TrackingOrderModel item3 = new TrackingOrderModel("Pickup Scheduled", "---");
        TrackingOrderModel item4 = new TrackingOrderModel("Pick Up", "---");
        TrackingOrderModel item5 = new TrackingOrderModel("In Transit", "---");
        TrackingOrderModel item6 = new TrackingOrderModel("Out for Delivery", "---");
        TrackingOrderModel item7 = new TrackingOrderModel("Delivered", "---");

        trackingOrderModelArrayList.add(item1);
        trackingOrderModelArrayList.add(item2);
        trackingOrderModelArrayList.add(item3);
        trackingOrderModelArrayList.add(item4);
        trackingOrderModelArrayList.add(item5);
        trackingOrderModelArrayList.add(item6);
        trackingOrderModelArrayList.add(item7);

        trackingOrderAdapter.notifyDataSetChanged();
    }
}