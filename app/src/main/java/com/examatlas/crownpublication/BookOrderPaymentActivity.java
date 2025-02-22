package com.examatlas.crownpublication;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.crownpublication.Adapter.extraAdapter.BookOrderSummaryItemsDetailsRecyclerViewAdapter;
import com.examatlas.crownpublication.Models.extraModels.BookOrderSummaryItemsDetailsRecyclerViewModel;
import com.examatlas.crownpublication.Models.extraModels.OrderItemsArrayListModel;
import com.examatlas.crownpublication.Utils.Constant;
import com.examatlas.crownpublication.Utils.MySingleton;
import com.examatlas.crownpublication.Utils.SessionManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BookOrderPaymentActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    String orderId,razorpayOrderID;
    int totalAmount;
    SessionManager sessionManager;
    String authToken,userMobile,userEmail,userID;
    ArrayList<BookOrderSummaryItemsDetailsRecyclerViewModel> bookOrderSummaryItemsDetailsRecyclerViewModelArrayList = new ArrayList<>();
    RecyclerView bookItemsSummaryRecyclerView;
    BookOrderSummaryItemsDetailsRecyclerViewModel bookOrderSummaryItemsDetailsRecyclerViewModel;
    BookOrderSummaryItemsDetailsRecyclerViewAdapter bookOrderSummaryItemsDetailsRecyclerViewAdapter;
    TextView referenceNoTxt,orderIdTxt,totalAmountTxt,statusTxt,paymentMethodTxt,nameTxt,shippingToTxt;
    ImageView backImgBtn,copyImgBtn;
    Button backToHomeBtn;
    RelativeLayout mainLayout;
    ProgressBar progressBar;
    String paymentStatusMessageStr;
    Dialog verifyingProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order_payment);
        mainLayout = findViewById(R.id.mainLayout);
        progressBar = findViewById(R.id.progressBar);

        Checkout.preload(getApplicationContext());
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_live_B3tlB0LBuRXlgF");

        bookItemsSummaryRecyclerView = findViewById(R.id.bookItemsSummaryRecyclerView);
        bookItemsSummaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookOrderSummaryItemsDetailsRecyclerViewModelArrayList = new ArrayList<>();

        backImgBtn = findViewById(R.id.backImgBtn);
        copyImgBtn = findViewById(R.id.copyImgBtn);
        backToHomeBtn = findViewById(R.id.backToHomeBtn);

        referenceNoTxt = findViewById(R.id.referenceNoTxt);
        orderIdTxt = findViewById(R.id.orderIdTxt);
        totalAmountTxt = findViewById(R.id.priceTxt);
        statusTxt = findViewById(R.id.paidTxt);
        paymentMethodTxt = findViewById(R.id.methodTxt);
        nameTxt = findViewById(R.id.nameTxt);
        shippingToTxt = findViewById(R.id.shippingToTxtDisplay);

//        totalAmount = Integer.parseInt(Objects.requireNonNull(getIntent().getStringExtra("amount")));
        totalAmount = 1;
        orderId = Objects.requireNonNull(getIntent().getStringExtra("orderId"));
        razorpayOrderID = Objects.requireNonNull(getIntent().getStringExtra("razorpayOrderId"));

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");
        userMobile = sessionManager.getUserData().get("mobile");
        userEmail = sessionManager.getUserData().get("email");
        userID = sessionManager.getUserData().get("user_id");

        try {
            JSONObject options = new JSONObject();

            options.put("name", "Crown Publications");
            options.put("description", "Reference No. #123456");
            options.put("image", "http://example.com/image/rzp.jpg");
            options.put("order_id", razorpayOrderID);
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", totalAmount);
            options.put("prefill.email", userEmail);
            options.put("prefill.contact",userMobile);
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(this, options);

        } catch(Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        copyImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) BookOrderPaymentActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Order ID", orderId);
                clipboard.setPrimaryClip(clip);

                Toast.makeText(BookOrderPaymentActivity.this, "Copied to clipboard: " + orderId, Toast.LENGTH_LONG).show();
            }
        });

        backImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookOrderPaymentActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
        backToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookOrderPaymentActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onPaymentSuccess(String razorpayPaymentID, PaymentData paymentData) {
//        getOrderDetails(razorpayPaymentID,paymentData);
        verifyingProgressDialog = new Dialog(BookOrderPaymentActivity.this);
        verifyingProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        verifyingProgressDialog.setContentView(R.layout.progress_bar_drawer);
        verifyingProgressDialog.setCancelable(false);
        verifyingProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        verifyingProgressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
        verifyingProgressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
        verifyingProgressDialog.show();
        verifyPaymentStatus(razorpayPaymentID,paymentData);
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(BookOrderPaymentActivity.this, CreateDeliveryAddressActivity.class);
        startActivity(intent);
        finish();
    }

    private void verifyPaymentStatus(String razorpayPaymentID, PaymentData paymentData) {
        String orderDetailsURL = Constant.BASE_URL + "payment/verify";
        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("razorpay_payment_id", razorpayPaymentID);
            jsonBody.put("razorpay_order_id", razorpayOrderID);
            jsonBody.put("razorpay_signature", paymentData.getSignature());
            // Add any other data needed for verification

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, orderDetailsURL, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("responseData", response.toString());
                            try {
                                boolean success = response.getBoolean("success");
                                if (success) {
                                    paymentStatusMessageStr = response.getString("message");
                                    JSONObject dataObj = response.getJSONObject("data");
                                    orderId = dataObj.getString("orderId");
                                    clearCartApi();
                                } else {
                                    // Handle failure case
                                }
                            } catch (JSONException e) {
                                Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMessage = "Error: " + error.toString();
                    if (error.networkResponse != null) {
                        try {
                            // Parse the error response
                            String jsonError = new String(error.networkResponse.data);
                            JSONObject jsonObject = new JSONObject(jsonError);
                            String message = jsonObject.optString("message", "Unknown error");
                            // Now you can use the message
                            Log.e("Error message" , message);
                            Toast.makeText(BookOrderPaymentActivity.this, message, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
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
            MySingleton.getInstance(BookOrderPaymentActivity.this).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Error creating JSON: " + e.getMessage());
        }
    }
    private void clearCartApi() {
        String clearCartURL = Constant.BASE_URL + "cart/clear";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, clearCartURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("responseData", response.toString());
                        try {
                            String success = response.getString("success");
                            if (success.equals("true")) {
                                Intent intent = new Intent(BookOrderPaymentActivity.this, OrderSuccessFullyPlacedActivity.class);
                                Toast.makeText(BookOrderPaymentActivity.this, paymentStatusMessageStr, Toast.LENGTH_SHORT).show();
                                verifyingProgressDialog.dismiss();
                                startActivity(intent);
                                finish();
                            } else {
                                // Handle failure case
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        // Now you can use the message
                        Toast.makeText(BookOrderPaymentActivity.this, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
        MySingleton.getInstance(BookOrderPaymentActivity.this).addToRequestQueue(jsonObjectRequest);
    }
//    private void getOrderDetails(String razorpayPaymentID, PaymentData paymentData) {
//        String orderDetailsURL = Constant.BASE_URL + "payment/getOneOrderByUserId/" + userID;
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, orderDetailsURL, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.e("responseData", response.toString());
//                        try {
//                            String status = response.getString("success");
//                            if (status.equals("true")) {
//
//                                JSONObject jsonObject1 = response.getJSONObject("order");
//
//                                String orderID = jsonObject1.getString("_id");
//                                String shipRocketOrderId = jsonObject1.getString("orderId");
//                                String totalAmount = jsonObject1.getString("totalAmount");
//                                String finalAmount = jsonObject1.getString("finalAmount");
//                                String paymentMethod = jsonObject1.getString("paymentMethod");
//                                String paymentStatus = jsonObject1.getString("status");
//                                String billingIdOfThisOrder = jsonObject1.getString("shippingDetailId");
//                                String billingDetailId = jsonObject1.getString("billingDetailId");
//                                String isShippingBillingSame = jsonObject1.getString("isShippingBillingSame");
//                                String razorpayOrderId = jsonObject1.getString("razorpayOrderId");
//                                String createdAt = jsonObject1.getString("createdAt");
//
//                                orderID = "Order ID: " + orderID;
//
//                                referenceNoTxt.setText(orderID);
//                                orderIdTxt.setText(orderID);
//
//                                totalAmountTxt.setText("₹ " +finalAmount);
//
//                                statusTxt.setText(paymentStatus);
//                                if (paymentStatus.equalsIgnoreCase("paid")){
//                                    statusTxt.setTextColor(getResources().getColor(R.color.green));
//                                }else if(paymentStatus.equalsIgnoreCase("pending")){
//                                    statusTxt.setTextColor(getResources().getColor(R.color.mat_yellow));
//                                }else {
//                                    statusTxt.setTextColor(getResources().getColor(R.color.red));
//                                }
//                                paymentMethodTxt.setText(paymentMethod);
//
//                                JSONObject jsonObject2 = jsonObject1.getJSONObject("shippingAddress");
//
//                                String addressType = jsonObject2.getString("addressType");
//                                String firstName = jsonObject2.getString("firstName");
//                                String lastName = jsonObject2.getString("lastName");
//                                String country = jsonObject2.getString("country");
//                                String streetAddress = jsonObject2.getString("streetAddress");
//                                String apartment = jsonObject2.getString("apartment");
//                                String city = jsonObject2.getString("city");
//                                String state = jsonObject2.getString("state");
//                                String zipCode = jsonObject2.getString("pinCode");
//                                String phone = jsonObject2.getString("phone");
//                                String email = jsonObject2.getString("email");
//
//                                String completeName = firstName + " " + lastName;
//                                String completeAddress = apartment + ", " + streetAddress + ", " + city + ", " + state + ", " + zipCode + ", " + country + ", " + phone + ", " + email + ".";
//
//                                nameTxt.setText(completeName);
//                                shippingToTxt.setText(completeAddress);
//
//
//
//                                JSONArray jsonArray2 = jsonObject1.getJSONArray("items");
//                                bookOrderSummaryItemsDetailsRecyclerViewModelArrayList = new ArrayList<>();
//                                for (int j = 0; j < jsonArray2.length(); j++) {
//                                    JSONObject jsonObject3 = jsonArray2.getJSONObject(j);
//
//                                    String itemId = jsonObject3.getString("_id");
//                                    String quantity = jsonObject3.getString("quantity");
//                                    String isInCart = jsonObject3.optString("IsInCart");
//
//                                    JSONObject jsonObject4 = jsonObject3.getJSONObject("bookId");
//
//                                    String title = jsonObject4.getString("title");
//                                    String sellPrice = jsonObject4.getString("sellPrice");
//
//                                    BookOrderSummaryItemsDetailsRecyclerViewModel orderItemsArrayListModel = new BookOrderSummaryItemsDetailsRecyclerViewModel(title,sellPrice,quantity);
//                                    bookOrderSummaryItemsDetailsRecyclerViewModelArrayList.add(orderItemsArrayListModel);
//                                }
//                                if (!bookOrderSummaryItemsDetailsRecyclerViewModelArrayList.isEmpty()) {
//                                    mainLayout.setVisibility(View.VISIBLE);
//                                    progressBar.setVisibility(View.GONE);
//                                    bookOrderSummaryItemsDetailsRecyclerViewAdapter = new BookOrderSummaryItemsDetailsRecyclerViewAdapter(BookOrderPaymentActivity.this, bookOrderSummaryItemsDetailsRecyclerViewModelArrayList);
//                                    bookItemsSummaryRecyclerView.setAdapter(bookOrderSummaryItemsDetailsRecyclerViewAdapter);
//                                }else {
//                                    mainLayout.setVisibility(View.GONE);
//                                    progressBar.setVisibility(View.VISIBLE);
//                                }
//                            } else {
//                                // Handle the case where success is not true
//                                Toast.makeText(BookOrderPaymentActivity.this, "Order retrieval failed", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
//                            Toast.makeText(BookOrderPaymentActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("onErrorResponse", "Error: " + error.toString());
//                String errorMessage = "Error retrieving order details.";
//                if (error.networkResponse != null) {
//                    try {
//                        // Parse the error response
//                        String jsonError = new String(error.networkResponse.data);
//                        JSONObject jsonObject = new JSONObject(jsonError);
//                        String message = jsonObject.optString("message", "Unknown error");
//                        errorMessage = message; // Update error message if available
//                    } catch (Exception e) {
//                        Log.e("JSON_ERROR", "Error parsing error JSON: " + e.getMessage());
//                    }
//                }
//                Toast.makeText(BookOrderPaymentActivity.this, errorMessage, Toast.LENGTH_LONG).show();
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                headers.put("Authorization", "Bearer " + authToken);
//                return headers;
//            }
//        };
//        MySingleton.getInstance(BookOrderPaymentActivity.this).addToRequestQueue(jsonObjectRequest);
//    }
}