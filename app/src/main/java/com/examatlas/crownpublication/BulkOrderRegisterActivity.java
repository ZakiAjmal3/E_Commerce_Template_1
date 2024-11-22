package com.examatlas.crownpublication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.examatlas.crownpublication.Utils.Constant;
import com.examatlas.crownpublication.Utils.MySingleton;
import com.examatlas.crownpublication.Utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class BulkOrderRegisterActivity extends AppCompatActivity {
    ImageView backBtn;
    EditText storeNameEditText, personNameEditText, addressEditText, cityNameEditText, stateNameEditText, contactNumberEditText, emailAddressEditText, messageEditText;
    Button submitBtn;
    SessionManager sessionManager;
    String authToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulk_order_register);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");

        storeNameEditText = findViewById(R.id.authorNameEditText);
        personNameEditText = findViewById(R.id.positionEditText);
        addressEditText = findViewById(R.id.addressEditText);
        cityNameEditText = findViewById(R.id.cityNameEditText);
        stateNameEditText = findViewById(R.id.stateNameEditText);
        contactNumberEditText = findViewById(R.id.contactNumberEditText);
        emailAddressEditText = findViewById(R.id.emailAddressEditText);
        messageEditText = findViewById(R.id.messageEditText);

        submitBtn = findViewById(R.id.submitBtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String storeName = storeNameEditText.getText().toString();
                String personName = personNameEditText.getText().toString();
                String address = addressEditText.getText().toString();
                String cityName = cityNameEditText.getText().toString();
                String stateName = stateNameEditText.getText().toString();
                String contactNumber = contactNumberEditText.getText().toString();
                String emailAddress = emailAddressEditText.getText().toString();
                String message = messageEditText.getText().toString();

                if (storeName.isEmpty() || personName.isEmpty() || address.isEmpty() || cityName.isEmpty() || stateName.isEmpty() || contactNumber.isEmpty() || emailAddress.isEmpty() || message.isEmpty()) {
                    Toast.makeText(BulkOrderRegisterActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }else {
                    sendBulkOrder(storeName, personName, address, cityName, stateName, contactNumber, emailAddress, message);
                }
            }
        });

    }
    private void sendBulkOrder(String storeName, String personName, String address, String cityName, String stateName, String contactNumber, String emailAddress, String message) {
        String bulkOrderURL = Constant.BASE_URL + "bulkorder/createBulkOrder";

        // Create the request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, bulkOrderURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response
                        try {
                            // Parse the response (if it's a JSON object or just a simple string)
                            JSONObject jsonObject = new JSONObject(response);
                            Boolean responseStatus = jsonObject.getBoolean("status");
                            String message = jsonObject.getString("message");

                            if (responseStatus) {
                                Toast.makeText(BulkOrderRegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(BulkOrderRegisterActivity.this,DashboardActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Handle failure or error response
                                Toast.makeText(BulkOrderRegisterActivity.this, "Failed to create bulk order: " + responseStatus, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSONException", e.toString());
                            Toast.makeText(BulkOrderRegisterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        Log.e("OnErrorResponse", error.toString());
                        Toast.makeText(BulkOrderRegisterActivity.this, "Error: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Set the parameters for the POST request
                Map<String, String> params = new HashMap<>();
                params.put("storeName", storeName);
                params.put("personName", personName);
                params.put("location", address);
                params.put("city", cityName);
                params.put("state", stateName);
                params.put("contactNumber", contactNumber);
                params.put("email", emailAddress);
                params.put("message", message);
                return params;
            }

            @Override
            public String getBodyContentType() {
                // Set content type to application/x-www-form-urlencoded
                return "application/x-www-form-urlencoded";
            }
        };

        // Add the request to the Volley request queue
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}