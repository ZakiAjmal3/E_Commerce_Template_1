package com.examatlas.crownpublication;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.crownpublication.Adapter.CartViewAdapter;
import com.examatlas.crownpublication.Models.CartViewModel;
import com.examatlas.crownpublication.Models.extraModels.BookImageModels;
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

public class CartViewActivity extends AppCompatActivity {
    RecyclerView bookCartRecyclerView;
    CartViewAdapter cartViewAdapter;
    CartViewModel cartViewModel;
    ArrayList<CartViewModel> cartViewModelArrayList;
    SessionManager sessionManager;
    String cartUrl,authToken;
    RelativeLayout noDataLayout, priceDetailRelativeLayout,bottomStickyButtonLayout;
    ProgressBar progressBar;
    TextView priceItemsTxt,priceOriginalTxt,totalDiscountTxt,deliveryTxt,totalAmountTxt1,totalAmountTxt2;
    Button goToCheckOutBTn;
    public BottomNavigationView bottom_navigation;
    public String currentFrag = "CART";
    RelativeLayout topBar;
    ImageView imgMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_view);  bookCartRecyclerView = findViewById(R.id.cartItemRecycler);

        noDataLayout = findViewById(R.id.noDataLayout);
        priceDetailRelativeLayout = findViewById(R.id.priceRelativeLayout);
        bottomStickyButtonLayout = findViewById(R.id.bottomStickyRelativeLayout);
        progressBar = findViewById(R.id.cartProgress);
        goToCheckOutBTn = findViewById(R.id.gotoCheckOut);

        bottom_navigation = findViewById(R.id.bottom_navigation);
        topBar = findViewById(R.id.topBar);
        imgMenu = findViewById(R.id.imgMenu);

        priceItemsTxt = findViewById(R.id.priceAndItemstxt);
        priceOriginalTxt = findViewById(R.id.priceTxt);
        totalDiscountTxt = findViewById(R.id.discountTxt);
        deliveryTxt = findViewById(R.id.deliveryTxt);
        totalAmountTxt1 = findViewById(R.id.totalAmountPriceTxt);
        totalAmountTxt2 = findViewById(R.id.bottomStickyAmountTxt);

        sessionManager = new SessionManager(this);
        cartViewModelArrayList = new ArrayList<>();

        bookCartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartUrl = Constant.BASE_URL + "cart/get/" + sessionManager.getUserData().get("user_id");
        authToken = sessionManager.getUserData().get("authToken");
        fetchCartItems();

        goToCheckOutBTn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartViewActivity.this, CreateDeliveryAddressActivity.class);
                startActivity(intent);
            }
        });
        bottom_navigation.setSelectedItemId(R.id.cart);
        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    currentFrag = "HOME";
                    Intent intent = new Intent(CartViewActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                    topBar.setVisibility(View.VISIBLE);
                } else if (item.getItemId() == R.id.cart) {
                    bottom_navigation.setLabelFor(R.id.cart);
                    currentFrag = "CART";
                    topBar.setVisibility(View.VISIBLE);
                } else if (item.getItemId() == R.id.orderHistory) {
                    currentFrag = "ORDER";
                    topBar.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(CartViewActivity.this, OrderHistoryActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    currentFrag = "PROFILE";
                    Intent intent = new Intent(CartViewActivity.this, ProfileActivity.class);
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

    public void fetchCartItems() {
        // Create a JsonObjectRequest for the GET request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, cartUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");

                            if (status) {
                                bookCartRecyclerView.setVisibility(View.VISIBLE);
                                priceDetailRelativeLayout.setVisibility(View.VISIBLE);
                                bottomStickyButtonLayout.setVisibility(View.VISIBLE);
                                noDataLayout.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);

                                JSONObject jsonObject = response.getJSONObject("cart");
                                String cartId = jsonObject.getString("_id");
                                JSONArray jsonArray = jsonObject.getJSONArray("items");
                                cartViewModelArrayList.clear(); // Clear the list before adding new items
                                ArrayList<BookImageModels> bookImageArrayList = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    String bookIdData = jsonObject2.getString("bookId");
                                    if (bookIdData == null || bookIdData.equals("null"))
                                        continue;
                                    String itemId = jsonObject2.getString("_id");
                                    String quantity = jsonObject2.getString("quantity");

                                    JSONObject jsonObject3 = jsonObject2.getJSONObject("bookId");
                                    String bookId = jsonObject3.getString("_id");
                                    String type  = jsonObject3.getString("type");
                                    String title = jsonObject3.getString("title");
                                    String keyword = jsonObject3.getString("keyword");
                                    String stock = jsonObject3.getString("stock");
                                    String price = jsonObject3.getString("price");
                                    String sellPrice = jsonObject3.getString("sellPrice");
                                    String content = jsonObject3.getString("content");
                                    String author = jsonObject3.getString("author");
                                    String categoryId = jsonObject3.getString("categoryId");
                                    String subCategoryId = jsonObject3.getString("subCategoryId");
                                    String subjectId = jsonObject3.getString("subjectId");
                                    String createdDate = jsonObject3.getString("createdAt");
                                    String updatedAt = jsonObject3.getString("updatedAt");

                                    JSONArray jsonArray3 = jsonObject3.getJSONArray("images");

                                    for (int j = 0; j < jsonArray3.length(); j++) {
                                        JSONObject jsonObject4 = jsonArray3.getJSONObject(j);
                                        BookImageModels bookImageModels = new BookImageModels(
                                                jsonObject4.getString("url"),
                                                jsonObject4.getString("filename"),
                                                jsonObject4.getString("contentType"),
                                                jsonObject4.getString("size"), // Assuming size is an integer
                                                jsonObject4.getString("uploadDate"),
                                                jsonObject4.getString("_id")
                                        );
                                        bookImageArrayList.add(bookImageModels);
                                    }

                                    // Use StringBuilder for tags
                                    StringBuilder tags = new StringBuilder();
                                    JSONArray jsonArray1 = jsonObject3.getJSONArray("tags");
                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        String singleTag = jsonArray1.getString(j);
                                        tags.append(singleTag).append(", ");
                                    }
                                    // Remove trailing comma and space if any
                                    if (tags.length() > 0) {
                                        tags.setLength(tags.length() - 2);
                                    }

                                    cartViewModel = new CartViewModel(cartId, itemId, bookId,type, title, keyword,stock, price, sellPrice, content, author, categoryId,subCategoryId,subjectId, tags.toString(),bookImageArrayList, createdDate, updatedAt, quantity);
                                    cartViewModelArrayList.add(cartViewModel);
                                }
                                // If you have already created the adapter, just notify the change
                                if (cartViewModelArrayList.isEmpty()) {
                                    Toast.makeText(CartViewActivity.this, "654", Toast.LENGTH_LONG).show();

                                    noDataLayout.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    if (cartViewAdapter == null) {

                                        bookCartRecyclerView.setVisibility(View.VISIBLE);
                                        priceDetailRelativeLayout.setVisibility(View.VISIBLE);
                                        bottomStickyButtonLayout.setVisibility(View.VISIBLE);
                                        noDataLayout.setVisibility(View.GONE);
                                        progressBar.setVisibility(View.GONE);
                                        cartViewAdapter = new CartViewAdapter(CartViewActivity.this, cartViewModelArrayList);
                                        bookCartRecyclerView.setAdapter(cartViewAdapter);
                                    } else {
                                        cartViewAdapter.notifyDataSetChanged();
                                    }
                                }
                                setUpPriceDetails();
                            } else {
                                bookCartRecyclerView.setVisibility(View.GONE);
                                priceDetailRelativeLayout.setVisibility(View.GONE);
                                bottomStickyButtonLayout.setVisibility(View.GONE);
                                progressBar.setVisibility(View.GONE);
                                noDataLayout.setVisibility(View.VISIBLE);
                                // Handle the case where status is false
                                String message = response.getString("message");
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
                        Toast.makeText(CartViewActivity.this, message, Toast.LENGTH_LONG).show();

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
        MySingleton.getInstance(CartViewActivity.this).addToRequestQueue(jsonObjectRequest);
    }

    @SuppressLint("ResourceType")
    private void setUpPriceDetails() {

        int totalItems,totalOriginalPrice = 0,totalSellPrice = 0,totalDiscount = 0,totalDelivery = 0;

        totalItems = cartViewModelArrayList.size();

        for (int i = 0; i<cartViewModelArrayList.size(); i++){
            int origPrice = Integer.parseInt(cartViewModelArrayList.get(i).getQuantity()) * Integer.parseInt(cartViewModelArrayList.get(i).getPrice());
            int sellPrice = Integer.parseInt(cartViewModelArrayList.get(i).getQuantity()) * Integer.parseInt(cartViewModelArrayList.get(i).getSellPrice());
            totalOriginalPrice = totalOriginalPrice + origPrice;
            totalSellPrice = totalSellPrice + sellPrice;
        }

        totalDiscount = totalOriginalPrice - totalSellPrice;

        priceItemsTxt.setText("Price (" + totalItems + " items)");
        priceOriginalTxt.setText("₹ " +totalOriginalPrice);
        totalDiscountTxt.setText("- ₹ " +totalDiscount);
        totalDiscountTxt.setTextColor(Color.GREEN);
        totalAmountTxt1.setText("₹ " +totalSellPrice);
        totalAmountTxt2.setText("₹ " +totalSellPrice);

        if (totalSellPrice > 1000){
            deliveryTxt.setText("FREE DELIVERY");
            deliveryTxt.setTextColor(Color.GREEN);
        }else {
            deliveryTxt.setText("₹ " +100);
        }

    }
    Dialog drawerDialog;
    LinearLayout layoutHome, layoutCart, layoutOrderHistory, layoutLogout, layoutShare, layoutAboutUs, layoutPrivacy, layoutTerms;
    TextView txtUsername, txtUserEmail;
    CircleImageView imgUser;
    MaterialCardView cardBack;

    private void showDrawerDialog() {
        drawerDialog = new Dialog(CartViewActivity.this);
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
                }
                Intent intent = new Intent(CartViewActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });
        layoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                if (!currentFrag.equals("CART")) {
                    currentFrag = "CART";
                    bottom_navigation.setSelectedItemId(R.id.cart);
                    bottom_navigation.setSelected(true);
                }
            }
        });
        layoutOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartViewActivity.this, OrderHistoryActivity.class);
                startActivity(intent);
                drawerDialog.dismiss();
                finish();
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
                        SessionManager sessionManager = new SessionManager(CartViewActivity.this);
                        sessionManager.logout();
                        Toast.makeText(CartViewActivity.this, "Logout Successfully...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CartViewActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(CartViewActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
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
    protected void onResume() {
        super.onResume();
        bottom_navigation.setSelectedItemId(R.id.cart);
    }
}