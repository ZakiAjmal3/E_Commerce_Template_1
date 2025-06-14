package com.examatlas.crownpublication;

import android.app.Dialog;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.crownpublication.Utils.Constant;
import com.examatlas.crownpublication.Utils.MySingleton;
import com.examatlas.crownpublication.Utils.SessionManager;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChoosingPaymentMethodActivity extends AppCompatActivity {
    ImageView backBtn,totalAmountArrow,codArrow,upiArrow;
    RelativeLayout priceHidingLayout,cashOnDeliveryRL,upiRL;
    LinearLayout totalAmountLinearLayout;
    Button codBTN,upiBTN;
    TextView priceAndItemstxt,hidingPriceTxt,hidingDiscountTxt,hidingShippingTxt,totalAmountTxt;
    CardView codCardView,upiCardView;
    boolean isTotalAmountClicked = false,isCODClicked = false,isUPIClicked = false,isEBookPresent = false;
    int totalSellingPrice = 0,shippingCharges = 0,totalDiscount = 0,finalAmount = 0;
    String billingIdStr = "0",codOrUpi = "",singleBookProductId = "",singleBookQuantity  = "";;
    int itemCount = 0;
    SessionManager sessionManager;
    String authToken;
    Dialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosing_payment_method);

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");

        backBtn = findViewById(R.id.backBtn);
        totalAmountArrow = findViewById(R.id.totalAmountArrow);
        priceHidingLayout = findViewById(R.id.priceHidingLayout);
        priceHidingLayout.setVisibility(View.GONE);
        totalAmountLinearLayout = findViewById(R.id.totalAmountLinearLayout);
        cashOnDeliveryRL = findViewById(R.id.cashOnDeliveryRL);
        upiRL = findViewById(R.id.upiRL);
        codArrow = findViewById(R.id.downArrowCODButton);
        upiArrow = findViewById(R.id.downArrowUPIButton);

        codBTN = findViewById(R.id.codBtn);
        upiBTN = findViewById(R.id.upiBtn);

        codCardView = findViewById(R.id.codButtonCardView);
        upiCardView = findViewById(R.id.upiButtonCardView);

        priceAndItemstxt = findViewById(R.id.priceAndItemstxt);
        hidingPriceTxt = findViewById(R.id.priceTxt);
        hidingDiscountTxt = findViewById(R.id.discountTxt);
        hidingShippingTxt = findViewById(R.id.deliveryTxt);
        totalAmountTxt = findViewById(R.id.totalAmountPriceTxt);

        totalSellingPrice = getIntent().getIntExtra("totalAmount", 0);   // Default value is 0
        shippingCharges = getIntent().getIntExtra("shippingCharges", 0);
        totalDiscount = getIntent().getIntExtra("discounts", 0);
        finalAmount = getIntent().getIntExtra("finalAmount", 0);
        itemCount = getIntent().getIntExtra("itemCount", 0);
        billingIdStr = getIntent().getStringExtra("addressId");
        isEBookPresent = getIntent().getBooleanExtra("isEBookPresent",false);

        singleBookQuantity = getIntent().getStringExtra("quantity");
//        Log.e("singleBookQuantity",String.valueOf(singleBookQuantity));
        singleBookProductId = getIntent().getStringExtra("productId");
//        Log.e("singleBookProductId",singleBookProductId);

        priceAndItemstxt.setText(String.valueOf("Price (" + itemCount + " items)"));
        hidingPriceTxt.setText(String.valueOf("₹" + (shippingCharges != 0 ? totalSellingPrice - 50 : totalSellingPrice)));
        hidingDiscountTxt.setText(String.valueOf("- ₹" + totalDiscount));
        hidingShippingTxt.setText(String.valueOf("₹" + shippingCharges));
        totalAmountTxt.setText("₹" + totalSellingPrice);

        if (isEBookPresent) {
            cashOnDeliveryRL.setVisibility(View.GONE);

            // Create a Snackbar with the message you want to display
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "For Cash on Delivery you need to remove ebook from cart",
                    Snackbar.LENGTH_LONG);

            // Set the background color of the Snackbar to red
            View snackbarView = snackbar.getView();
            snackbar.getView().setBackgroundColor(Color.RED);

            // Set the text color of the Snackbar message to white
            TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);

            // Show the Snackbar without any action button
            snackbar.show();
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        totalAmountLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isTotalAmountClicked){
                    priceHidingLayout.setVisibility(View.GONE);
                    totalAmountArrow.setImageResource(R.drawable.ic_down_blue);
                    isTotalAmountClicked = false;
                }else {
                    priceHidingLayout.setVisibility(View.VISIBLE);
                    totalAmountArrow.setImageResource(R.drawable.ic_up_blue);
                    isTotalAmountClicked = true;
                }
            }
        });
        cashOnDeliveryRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCODClicked){
                    codArrow.setImageResource(R.drawable.ic_down);
                    cashOnDeliveryRL.setBackgroundColor(ContextCompat.getColor(ChoosingPaymentMethodActivity.this,R.color.white));
                    codCardView.setVisibility(View.GONE);
                    isCODClicked = false;
                    upiArrow.setImageResource(R.drawable.ic_down);
                    upiRL.setBackgroundColor(ContextCompat.getColor(ChoosingPaymentMethodActivity.this,R.color.white));
                    upiCardView.setVisibility(View.GONE);
                    isUPIClicked = false;
                }else {
                    codArrow.setImageResource(R.drawable.ic_up);
                    cashOnDeliveryRL.setBackgroundColor(ContextCompat.getColor(ChoosingPaymentMethodActivity.this,R.color.light_dark_grey));
                    codCardView.setVisibility(View.VISIBLE);
                    isCODClicked = true;
                    upiArrow.setImageResource(R.drawable.ic_down);
                    upiRL.setBackgroundColor(ContextCompat.getColor(ChoosingPaymentMethodActivity.this,R.color.white));
                    upiCardView.setVisibility(View.GONE);
                    isUPIClicked = false;
                }
            }
        });
        upiRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isUPIClicked){
                    upiArrow.setImageResource(R.drawable.ic_down);
                    upiRL.setBackgroundColor(ContextCompat.getColor(ChoosingPaymentMethodActivity.this,R.color.white));
                    upiCardView.setVisibility(View.GONE);
                    isUPIClicked = false;
                    codArrow.setImageResource(R.drawable.ic_down);
                    cashOnDeliveryRL.setBackgroundColor(ContextCompat.getColor(ChoosingPaymentMethodActivity.this,R.color.white));
                    codCardView.setVisibility(View.GONE);
                    isCODClicked = false;
                }else {
                    upiArrow.setImageResource(R.drawable.ic_up);
                    upiRL.setBackgroundColor(ContextCompat.getColor(ChoosingPaymentMethodActivity.this,R.color.light_dark_grey));
                    upiCardView.setVisibility(View.VISIBLE);
                    isUPIClicked = true;
                    codArrow.setImageResource(R.drawable.ic_down);
                    cashOnDeliveryRL.setBackgroundColor(ContextCompat.getColor(ChoosingPaymentMethodActivity.this,R.color.white));
                    codCardView.setVisibility(View.GONE);
                    isCODClicked = false;
                }
            }
        });
        codBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codOrUpi = "COD";
                progressDialog = new Dialog(ChoosingPaymentMethodActivity.this);
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.progress_bar_drawer);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressDialog.show();
                if (isEBookPresent) {
                    orderCheckOut();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(ChoosingPaymentMethodActivity.this, "You can't purchase E-Book on Cash on Delivery", Toast.LENGTH_LONG).show();
                }
            }
        });
        upiBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codOrUpi = "Razorpay";
                progressDialog = new Dialog(ChoosingPaymentMethodActivity.this);
                progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                progressDialog.setContentView(R.layout.progress_bar_drawer);
                progressDialog.setCancelable(false);
                progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progressDialog.getWindow().setGravity(Gravity.CENTER); // Center the dialog
                progressDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT); // Adjust the size
                progressDialog.show();
                orderCheckOut();
            }
        });
    }
    public void orderCheckOut(){
        String checkOutURL = Constant.BASE_URL + "order/checkout";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("addressId",billingIdStr);
            jsonObject.put("discounts",0);
            jsonObject.put("finalAmount", 1);
            jsonObject.put("paymentMethod",codOrUpi);
            jsonObject.put("totalAmount",1);
            jsonObject.put("taxAmount",0);
            jsonObject.put("shippingCharges",0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Make the API request to the checkout URL
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, checkOutURL, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("responseData", response.toString());
                        try {
                            String status = response.getString("success");
                            JSONObject paymentObj = response.getJSONObject("payment");
                            String orderId = paymentObj.getString("orderId");
                            String razorpayOrderId = paymentObj.getString("razorpayOrderId");
                            String finalAmount = paymentObj.getString("finalAmount");
                            if (codOrUpi.equals("Razorpay")) {
                                Intent intent = new Intent(ChoosingPaymentMethodActivity.this, BookOrderPaymentActivity.class);
                                intent.putExtra("amount", finalAmount);
                                intent.putExtra("orderId", orderId);
                                intent.putExtra("razorpayOrderId", razorpayOrderId);
                                startActivity(intent);
                                finish();
                            }else {
                                confirmCODOrder(orderId);
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = error.toString();
                Toast.makeText(ChoosingPaymentMethodActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("onErrorResponse", errorMessage);
                if (error.networkResponse != null) {
                    try {
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        // Now you can use the message
                        Toast.makeText(ChoosingPaymentMethodActivity.this, message, Toast.LENGTH_LONG).show();

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

        // Add the request to the request queue
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
    private void confirmCODOrder(String orderId) {
        String orderCheckOutURL = Constant.BASE_URL + "order/checkout/cod";

        Log.e("sendingOTP method", orderCheckOutURL);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("orderId",orderId);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, orderCheckOutURL , jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
//                            Toast.makeText(CartViewActivity.this, "try", Toast.LENGTH_SHORT).show();
                            boolean status = response.getBoolean("success");
                            if (status) {
                                clearCartApi(orderId);
                            } else {
//                                Toast.makeText(CartViewActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
//                            Toast.makeText(CartViewActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(CartViewActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                        String errorMessage = "Error: " + error.toString();
                        if (error.networkResponse != null) {
                            try {
                                // Parse the error response
                                String jsonError = new String(error.networkResponse.data);
                                JSONObject jsonObject = new JSONObject(jsonError);
                                String message = jsonObject.optString("message", "Unknown error");
                                // Now you can use the message
                                Toast.makeText(ChoosingPaymentMethodActivity.this, message, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(ChoosingPaymentMethodActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        Log.e("BlogFetchError", errorMessage);
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
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
    private void clearCartApi(String orderId) {
        String clearCartURL = Constant.BASE_URL + "cart/clear";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, clearCartURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("responseData", response.toString());
                        try {
                            String success = response.getString("success");
                            if (success.equals("true")) {
                                sessionManager.setCartItemQuantity();
                                Intent intent = new Intent(ChoosingPaymentMethodActivity.this, OrderSuccessFullyPlacedActivity.class);
                                progressDialog.dismiss();
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
                        Toast.makeText(ChoosingPaymentMethodActivity.this, message, Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(ChoosingPaymentMethodActivity.this).addToRequestQueue(jsonObjectRequest);
    }
}