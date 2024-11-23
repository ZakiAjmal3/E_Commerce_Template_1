package com.examatlas.crownpublication;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.crownpublication.Utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONObject;

public class TrackinOrderActivity extends AppCompatActivity {

    private TextView orderStatusTextView, deliveryStatusTextView;
    private ProgressBar verticalProgressBar;
    private String shipmentId = "SHIPMENT_ID"; // Replace with actual shipment ID
    private String orderId = "ORDER_ID"; // Replace with actual order ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trackin_order);

        orderStatusTextView = findViewById(R.id.orderStatus);
        deliveryStatusTextView = findViewById(R.id.deliveryStatusText);
        verticalProgressBar = findViewById(R.id.verticalProgressBar);

        // Fetch tracking details from Shiprocket API
        getTrackingDetails(shipmentId, orderId);
    }
    private void getTrackingDetails(String shipmentId, String orderId) {
        String url = "https://apiv2.shiprocket.in/v1/courier/track?shipment_id=" + shipmentId + "&order_id=" + orderId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            if ("success".equals(status)) {
                                JSONArray trackingDetails = response.getJSONArray("tracking_details");

                                // Calculate the progress based on tracking details
                                int totalSteps = trackingDetails.length();
                                for (int i = 0; i < trackingDetails.length(); i++) {
                                    JSONObject trackingStep = trackingDetails.getJSONObject(i);
                                    String stepStatus = trackingStep.getString("status");
                                    String location = trackingStep.getString("location");
                                    String timestamp = trackingStep.getString("timestamp");

                                    // Update status text dynamically
                                    deliveryStatusTextView.setText("Status: " + stepStatus + "\nLocation: " + location + "\nTime: " + timestamp);

                                    // Calculate the progress percentage based on the current step
                                    int progress = (int) (((float) (i + 1) / totalSteps) * 100);
                                    verticalProgressBar.setProgress(progress);
                                }

                                // Set the final status as "Delivered" if the last step is delivered
                                JSONObject lastStep = trackingDetails.getJSONObject(trackingDetails.length() - 1);
                                String finalStatus = lastStep.getString("status");
                                if ("Delivered".equals(finalStatus)) {
                                    orderStatusTextView.setText("Delivered");
                                } else {
                                    orderStatusTextView.setText("Order Placed Successfully");
                                }

                            } else {
                                Toast.makeText(TrackinOrderActivity.this, "Error: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(TrackinOrderActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TrackinOrderActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
        MySingleton.getInstance(this).addToRequestQueue(request);
    }
}