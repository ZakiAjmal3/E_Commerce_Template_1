package com.examatlas.crownpublication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.crownpublication.Adapter.OrderHistoryAdapter;
import com.examatlas.crownpublication.Models.OrderHistoryModel;
import com.examatlas.crownpublication.Models.extraModels.OrderItemsArrayListModel;
import com.examatlas.crownpublication.Utils.Constant;
import com.examatlas.crownpublication.Utils.MySingleton;
import com.examatlas.crownpublication.Utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderHistoryActivity extends AppCompatActivity {
    RelativeLayout topBar;
    ImageView imgMenu;
    public String currentFrag = "ORDER";
    SessionManager sessionManager;
    String authToken,userID;
    RecyclerView orderItemsSummaryRecyclerView;
    OrderHistoryModel orderHistoryModel;
    OrderHistoryAdapter orderHistoryAdapter;
    ArrayList<OrderHistoryModel> orderHistoryModelArrayList;
    ArrayList<OrderItemsArrayListModel> orderItemsArrayListModelArrayList;
    ProgressBar progressBar;
    TextView noDataTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");
        userID = sessionManager.getUserData().get("user_id");

        progressBar = findViewById(R.id.progressBar);
        noDataTxt = findViewById(R.id.noDataTxt);

        topBar = findViewById(R.id.topBar);
        imgMenu = findViewById(R.id.imgMenu);

        orderItemsSummaryRecyclerView = findViewById(R.id.orderHistoryRecyclerView);
        orderItemsSummaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderHistoryModelArrayList = new ArrayList<>();

        fetchAllOrderHistory();

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDrawerDialog();
            }
        });
    }

    Dialog drawerDialog;
    LinearLayout layoutHome, layoutCart, layoutOrderHistory,layoutProfile, layoutLogout, layoutShare, layoutAboutUs, layoutPrivacy, layoutTerms;
    TextView txtUsername, txtUserEmail;
    CircleImageView imgUser;
    MaterialCardView cardBack;

    private void showDrawerDialog() {
        drawerDialog = new Dialog(OrderHistoryActivity.this);
        drawerDialog.setContentView(R.layout.custom_drawer_dialog);
        drawerDialog.setCancelable(true);

        layoutHome = drawerDialog.findViewById(R.id.layoutHome);
        layoutCart = drawerDialog.findViewById(R.id.layoutCart);
        layoutOrderHistory = drawerDialog.findViewById(R.id.layoutOrderHistory);
        layoutProfile = drawerDialog.findViewById(R.id.layoutProfile);

        layoutLogout = drawerDialog.findViewById(R.id.layoutLogout);
        layoutShare = drawerDialog.findViewById(R.id.layoutShare);
        layoutAboutUs = drawerDialog.findViewById(R.id.layoutAboutUs);
        layoutPrivacy = drawerDialog.findViewById(R.id.layoutPrivacy);
        layoutTerms = drawerDialog.findViewById(R.id.layoutTerms);
        txtUsername = drawerDialog.findViewById(R.id.txtUsername);
        txtUserEmail = drawerDialog.findViewById(R.id.txtUserEmail);
        cardBack = drawerDialog.findViewById(R.id.cardBack);
        imgUser = drawerDialog.findViewById(R.id.imgUser);

        txtUsername.setText(sessionManager.getUserData().get("name"));
        txtUserEmail.setText(sessionManager.getUserData().get("email"));

        layoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                Intent intent = new Intent(OrderHistoryActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
        layoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                Intent intent = new Intent(OrderHistoryActivity.this, CartViewActivity.class);
                startActivity(intent);
                finish();
            }
        });

        layoutOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                Intent intent = new Intent(OrderHistoryActivity.this, OrderHistoryActivity.class);
                startActivity(intent);
                finish();
            }
        });
        layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderHistoryActivity.this, ProfileActivity.class);
                startActivity(intent);
                drawerDialog.dismiss();
            }
        });
        cardBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
            }
        });
        layoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareApplication();
            }
        });

        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quitDialog();
            }
        });

        drawerDialog.show();
        drawerDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        drawerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        drawerDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        drawerDialog.getWindow().setGravity(Gravity.TOP);
        drawerDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            drawerDialog.getWindow().setStatusBarColor(getColor(R.color.seed));
        }
    }

    private void quitDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout this session?")
                .setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SessionManager sessionManager = new SessionManager(OrderHistoryActivity.this);
                        sessionManager.logout();
                        Toast.makeText(OrderHistoryActivity.this, "Logout Successfully...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(OrderHistoryActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(OrderHistoryActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }).show();

    }
    private void shareApplication() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
            String shareMessage = "The best mock test application currently i am using right now for my exam preparation. You should download it and try some of its cool features.";
            shareMessage = shareMessage + "http://play.google.com/store/apps/details?id=" + getApplication().getPackageName() + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }
    public void fetchAllOrderHistory() {
        String fetchOrderURl = Constant.BASE_URL + "order";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fetchOrderURl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("responseData", response.toString());
                        try {
                            String status = response.getString("success");
                            if (status.equals("true")) {

                                JSONArray jsonArray1 = response.getJSONArray("data");
                                for (int i = 0; i < jsonArray1.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(i);

                                    String orderID = jsonObject1.getString("orderId");
                                    String totalAmount = jsonObject1.getString("totalAmount");
                                    String finalAmount = jsonObject1.getString("finalAmount");
                                    String paymentMethod = jsonObject1.getString("paymentMethod");
                                    String paymentStatus = jsonObject1.getString("status");
                                    String createdAt = jsonObject1.getString("createdAt");

                                    // Extract shipping address details
                                    JSONObject jsonObject2 = jsonObject1.optJSONObject("shippingAddress");
                                    if (jsonObject2 == null) {
                                        // Handle case where shippingAddress is missing or null
                                        Log.e("ShippingAddress", "Shipping address is null or missing for order: " + orderID);
                                        continue; // Skip this order if shippingAddress is missing
                                    }

                                    String addressType = jsonObject2.getString("addressType");
                                    String firstName = jsonObject2.getString("firstName");
                                    String lastName = jsonObject2.getString("lastName");
                                    String country = jsonObject2.getString("country");
                                    String streetAddress = jsonObject2.getString("streetAddress");
                                    String apartment = jsonObject2.optString("apartment", ""); // Safely handle empty apartment
                                    String city = jsonObject2.getString("city");
                                    String state = jsonObject2.getString("state");
                                    String zipCode = jsonObject2.getString("pinCode");
                                    String phone = jsonObject2.getString("phone");
                                    String email = jsonObject2.getString("email");

                                    String completeName = firstName + " " + lastName;
                                    String completeAddress = (apartment.isEmpty() ? "" : apartment + ", ") + streetAddress + ", " + city + ", " + state + ", " + zipCode + ", " + country + ", " + phone + ", " + email + ".";

                                    // Extract order items
                                    JSONArray jsonArray2 = jsonObject1.getJSONArray("items");
                                    ArrayList<OrderItemsArrayListModel> orderItemsArrayListModelArrayList = new ArrayList<>();
                                    for (int j = 0; j < jsonArray2.length(); j++) {
                                        JSONObject jsonObject3 = jsonArray2.getJSONObject(j);

                                        JSONObject jsonObject4 = jsonObject3.getJSONObject("product");

                                        String bookId = jsonObject4.getString("_id");
                                        String title = jsonObject4.getString("title"); // Correct field
                                        String sellPrice = jsonObject4.getString("sellingPrice");
                                        String quantity = jsonObject3.getString("quantity");

                                        OrderItemsArrayListModel orderItemsArrayListModel = new OrderItemsArrayListModel(bookId, title, sellPrice, quantity);
                                        orderItemsArrayListModelArrayList.add(orderItemsArrayListModel);
                                    }
                                    String razorpayOrderId = jsonObject1.getJSONObject("payment").getString("razorpayOrderId");
                                    String shipRocketOrderId = "N/A";

                                    if (jsonObject1.has("trackingDetail") && !jsonObject1.isNull("trackingDetail")) {
                                        JSONObject trackingDetail = jsonObject1.optJSONObject("trackingDetail");
                                        if (trackingDetail != null) {
                                            // Check if "shipment_id" exists and is not empty
                                            shipRocketOrderId = trackingDetail.optString("shipment_id", "N/A");
                                        }
                                    }
                                    // Create and add OrderHistoryModel to the list
                                    OrderHistoryModel orderHistoryModel = new OrderHistoryModel(orderID, shipRocketOrderId, totalAmount,finalAmount, paymentMethod, paymentStatus, addressType, completeName, completeAddress, razorpayOrderId, createdAt, orderItemsArrayListModelArrayList);
                                    orderHistoryModelArrayList.add(orderHistoryModel);
                                }

                                // Check if orders are found
                                if (!orderHistoryModelArrayList.isEmpty()) {
                                    progressBar.setVisibility(View.GONE);
                                    noDataTxt.setVisibility(View.GONE);
                                    orderItemsSummaryRecyclerView.setVisibility(View.VISIBLE);
                                    orderHistoryAdapter = new OrderHistoryAdapter(orderHistoryModelArrayList, OrderHistoryActivity.this);
                                    orderItemsSummaryRecyclerView.setAdapter(orderHistoryAdapter);
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    orderItemsSummaryRecyclerView.setVisibility(View.GONE);
                                    noDataTxt.setVisibility(View.VISIBLE);
                                }
                            } else {
                                progressBar.setVisibility(View.GONE);
                                orderItemsSummaryRecyclerView.setVisibility(View.GONE);
                                noDataTxt.setVisibility(View.VISIBLE);
                                Toast.makeText(OrderHistoryActivity.this, "Order retrieval failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                            Toast.makeText(OrderHistoryActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", "Error: " + error.toString());
                String errorMessage = "Error retrieving order details.";
                if (error.networkResponse != null) {
                    try {
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        errorMessage = message;
                    } catch (Exception e) {
                        Log.e("JSON_ERROR", "Error parsing error JSON: " + e.getMessage());
                    }
                }
                Toast.makeText(OrderHistoryActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };
        MySingleton.getInstance(OrderHistoryActivity.this).addToRequestQueue(jsonObjectRequest);
    }
}