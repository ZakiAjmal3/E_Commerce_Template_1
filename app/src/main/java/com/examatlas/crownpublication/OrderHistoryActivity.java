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
    public BottomNavigationView bottom_navigation;
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

        bottom_navigation = findViewById(R.id.bottom_navigation);
        topBar = findViewById(R.id.topBar);
        imgMenu = findViewById(R.id.imgMenu);

        orderItemsSummaryRecyclerView = findViewById(R.id.orderHistoryRecyclerView);
        orderItemsSummaryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderHistoryModelArrayList = new ArrayList<>();

        fetchAllOrderHistory();

        bottom_navigation.setSelectedItemId(R.id.orderHistory);
        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    currentFrag = "HOME";
                    Intent intent = new Intent(OrderHistoryActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                    topBar.setVisibility(View.VISIBLE);
                } else if (item.getItemId() == R.id.cart) {
                    bottom_navigation.setLabelFor(R.id.cart);
                    currentFrag = "CART";
                    topBar.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(OrderHistoryActivity.this, CartViewActivity.class);
                    startActivity(intent);
                    finish();
                } else if (item.getItemId() == R.id.orderHistory) {
                    currentFrag = "ORDER";
                    topBar.setVisibility(View.VISIBLE);
                } else {
                    currentFrag = "PROFILE";
                    Intent intent = new Intent(OrderHistoryActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    finish();
                    topBar.setVisibility(View.GONE);
                }
                return true;
            }
        });
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDrawerDialog();
            }
        });
    }

    Dialog drawerDialog;
    LinearLayout layoutHome, layoutCart, layoutOrderHistory, layoutLogout, layoutShare, layoutAboutUs, layoutPrivacy, layoutTerms;
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
                if (!currentFrag.equals("HOME")) {
                    currentFrag = "HOME";
//                    loadFragment(new AllBooksFragment());
                    bottom_navigation.setSelectedItemId(R.id.home);
                    bottom_navigation.setSelected(true);
                    Intent intent = new Intent(OrderHistoryActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });
        layoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                if (!currentFrag.equals("CART")) {
                    currentFrag = "CART";
//                    loadFragment(new CartViewFragment());
                    bottom_navigation.setSelectedItemId(R.id.cart);
                    bottom_navigation.setSelected(true);
                    Intent intent = new Intent(OrderHistoryActivity.this, CartViewActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        layoutOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
    public void fetchAllOrderHistory(){
        String fetchOrderURl = Constant.BASE_URL + "payment/getOrdersByUserId/" + userID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, fetchOrderURl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("responseData", response.toString());
                        try {
                            String status = response.getString("success");
                            if (status.equals("true")) {

                                JSONArray jsonArray1 = response.getJSONArray("orders");
                                for (int i = 0; i < jsonArray1.length(); i++) {
                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(i);

                                    String orderID = jsonObject1.getString("_id");
                                    String totalAmount = jsonObject1.getString("totalAmount");
                                    String paymentMethod = jsonObject1.getString("paymentMethod");
                                    String paymentStatus = jsonObject1.getString("status");
                                    String billingIdOfThisOrder = jsonObject1.getString("billingDetailId");

                                    JSONObject jsonObject2 = jsonObject1.getJSONObject("billingDetail");

                                    String firstName = jsonObject2.getString("firstName");
                                    String lastName = jsonObject2.getString("lastName");
                                    String country = jsonObject2.getString("country");
                                    String streetAddress = jsonObject2.getString("streetAddress");
                                    String apartment = jsonObject2.getString("apartment");
                                    String city = jsonObject2.getString("city");
                                    String state = jsonObject2.getString("state");
                                    String zipCode = jsonObject2.getString("pinCode");
                                    String phone = jsonObject2.getString("phone");
                                    String email = jsonObject2.getString("email");

                                    String completeName = firstName + " " + lastName;
                                    String completeAddress = apartment + ", " + streetAddress + ", " + city + ", " + state + ", " + zipCode + ", " + country + ", " + phone + ", " + email + ".";

                                    JSONArray jsonArray2 = jsonObject1.getJSONArray("items");
                                    orderItemsArrayListModelArrayList = new ArrayList<>();
                                    for (int j = 0; j < jsonArray2.length(); j++) {
                                        JSONObject jsonObject3 = jsonArray2.getJSONObject(j);

                                        String cartId = jsonObject3.getString("cartId");
                                        String itemId = jsonObject3.getString("itemId");
                                        String itemName = jsonObject3.getString("title");
                                        String itemPrice = jsonObject3.getString("sellPrice");
                                        String itemQuantity = jsonObject3.getString("quantity");
                                        String bookId = jsonObject3.getString("bookId");

                                        OrderItemsArrayListModel orderItemsArrayListModel = new OrderItemsArrayListModel(cartId,itemId,itemName,itemPrice,itemQuantity,bookId);
                                        orderItemsArrayListModelArrayList.add(orderItemsArrayListModel);
                                    }

                                    String razorpay_order_id = jsonObject1.getString("razorpay_order_id");
                                    String razorpay_payment_id = jsonObject1.getString("razorpay_payment_id");
                                    String razorpay_signature = jsonObject1.getString("razorpay_signature");

                                    orderHistoryModel = new OrderHistoryModel(orderID,totalAmount,paymentMethod,paymentStatus,billingIdOfThisOrder,completeName,completeAddress,razorpay_order_id,razorpay_payment_id,razorpay_signature,orderItemsArrayListModelArrayList);
                                    orderHistoryModelArrayList.add(orderHistoryModel);
                                }if (!orderHistoryModelArrayList.isEmpty()) {
                                    progressBar.setVisibility(View.GONE);
                                    noDataTxt.setVisibility(View.GONE);
                                    orderItemsSummaryRecyclerView.setVisibility(View.VISIBLE);
                                    orderHistoryAdapter = new OrderHistoryAdapter(orderHistoryModelArrayList, OrderHistoryActivity.this);
                                    orderItemsSummaryRecyclerView.setAdapter(orderHistoryAdapter);
                                }else {
                                    progressBar.setVisibility(View.GONE);
                                    orderItemsSummaryRecyclerView.setVisibility(View.GONE);
                                    noDataTxt.setVisibility(View.VISIBLE);
                                }
                            }else {
                                progressBar.setVisibility(View.GONE);
                                orderItemsSummaryRecyclerView.setVisibility(View.GONE);
                                noDataTxt.setVisibility(View.VISIBLE);
                                Toast.makeText(OrderHistoryActivity.this, "Order retrieval failed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                            Toast.makeText(OrderHistoryActivity.this, "Parsing error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("onErrorResponse", "Error: " + error.toString());
                String errorMessage = "Error retrieving order details.";
                if (error.networkResponse != null) {
                    try {
                        // Parse the error response
                        String jsonError = new String(error.networkResponse.data);
                        JSONObject jsonObject = new JSONObject(jsonError);
                        String message = jsonObject.optString("message", "Unknown error");
                        errorMessage = message; // Update error message if available
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
    protected void onResume() {
        super.onResume();
        bottom_navigation.setSelectedItemId(R.id.orderHistory);
    }
}