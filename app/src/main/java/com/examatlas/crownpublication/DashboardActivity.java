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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.examatlas.crownpublication.Adapter.DashboardAdapter;
import com.examatlas.crownpublication.Models.DashboardModel;
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

public class DashboardActivity extends AppCompatActivity {
    private ImageSlider slider;
    RecyclerView allBooksRecyclerView;
    DashboardModel dashboardModel;
    DashboardAdapter dashboardAdapter;
    ArrayList<DashboardModel> dashboardModelArrayList;
    ProgressBar progressBar;
    RelativeLayout noDataLayout;
    ImageView imgMenu;
    RelativeLayout topBar;
    public BottomNavigationView bottom_navigation;
    public String currentFrag = "HOME";
    SessionManager sessionManager;
    String authToken;
    private final String bookURL = Constant.BASE_URL + "book/getAllBooks";
    private int currentPage = 1;
    private int totalPages = 1;
    private final int itemsPerPage = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dasbboard);


        bottom_navigation = findViewById(R.id.bottom_navigation);
        imgMenu = findViewById(R.id.imgMenu);
        topBar = findViewById(R.id.topBar);
        slider = findViewById(R.id.slider);

        noDataLayout = findViewById(R.id.noDataLayout);
        progressBar = findViewById(R.id.progressBar);
        allBooksRecyclerView = findViewById(R.id.booksRecycler);
        allBooksRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
        dashboardModelArrayList = new ArrayList<>();

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");

        ArrayList<SlideModel> sliderArrayList = new ArrayList<>();
        sliderArrayList.add(new SlideModel(R.drawable.image1, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image2, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image3, ScaleTypes.CENTER_CROP));
        slider.setImageList(sliderArrayList);

        bottom_navigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    currentFrag = "HOME";
                    topBar.setVisibility(View.VISIBLE);
                    getAllBooks();
                } else if (item.getItemId() == R.id.cart) {
                    currentFrag = "CART";
                    topBar.setVisibility(View.VISIBLE);
                    Intent intent = new Intent(DashboardActivity.this, CartViewActivity.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.orderHistory) {
                    currentFrag = "ORDER";
                    topBar.setVisibility(View.VISIBLE);
//                    loadFragment(new LiveCoursesFragment());
                } else {
                    currentFrag = "PROFILE";
                    topBar.setVisibility(View.GONE);
//                    loadFragment(new ProfileFragment());
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

//        // Handle back press
//        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                if (!(currentFragment instanceof AllBooksFragment)) {
//                    loadFragment(new AllBooksFragment());
//                    bottom_navigation.setSelectedItemId(R.id.home);
//                } else {
//                    setEnabled(false); // Disable the callback for default back press behavior
//                }
//            }
//        });

        getAllBooks();

    }

//    public void loadFragment(Fragment fragment) {
//        bottom_navigation.setEnabled(false);
//        // Clear previous fragments if needed
//        if (currentFragment != null && currentFragment != fragment) {
//            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
//        }
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.container, fragment)
//                .commit();
//        currentFragment = fragment;
//    }

    private void getAllBooks() {
        String paginatedURL = bookURL + "?type=book&page=" + currentPage + "&per_page=" + itemsPerPage;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, paginatedURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            allBooksRecyclerView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("books");
                                dashboardModelArrayList.clear();

                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                    ArrayList<BookImageModels> bookImageArrayList = new ArrayList<>();
                                    JSONArray jsonArray1 = jsonObject2.getJSONArray("images");

                                    JSONObject jsonObject = response.getJSONObject("pagination");

                                    int totalRows = Integer.parseInt(jsonObject.getString("totalRows"));
                                    totalPages = Integer.parseInt(jsonObject.getString("totalPages"));
                                    currentPage = Integer.parseInt(jsonObject.getString("currentPage"));
                                    int pageSize = Integer.parseInt(jsonObject.getString("pageSize"));

                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        JSONObject jsonObject3 = jsonArray1.getJSONObject(j);
                                        BookImageModels bookImageModels = new BookImageModels(
                                                jsonObject3.getString("url"),
                                                jsonObject3.getString("filename"),
                                                jsonObject3.getString("contentType"),
                                                jsonObject3.getString("size"), // Assuming size is an integer
                                                jsonObject3.getString("uploadDate"),
                                                jsonObject3.getString("_id")
                                        );
                                        bookImageArrayList.add(bookImageModels);
                                    }
                                    DashboardModel model = new DashboardModel(
                                            jsonObject2.getString("_id"),
                                            jsonObject2.getString("type"),
                                            jsonObject2.getString("title"),
                                            jsonObject2.getString("keyword"),
                                            jsonObject2.getString("stock"),
                                            jsonObject2.getString("price"),
                                            jsonObject2.getString("sellPrice"),
                                            jsonObject2.getString("content"),
                                            jsonObject2.getString("author"),
                                            jsonObject2.getString("categoryId"),
                                            jsonObject2.getString("subCategoryId"),
                                            jsonObject2.getString("subjectId"),
                                            parseTags(jsonObject2.getJSONArray("tags")), // Ensure this method is implemented correctly
                                            jsonObject2.getString("bookUrl"),
                                            bookImageArrayList,
                                            jsonObject2.getString("createdAt"),
                                            jsonObject2.getString("updatedAt"),
                                            jsonObject2.getString("isInCart"),
                                            jsonObject2.getString("isInWishList"),
                                            totalRows,totalPages,currentPage,pageSize
                                    );
                                    dashboardModelArrayList.add(model);
                                }
                                updateUI();
                                if (dashboardModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                    allBooksRecyclerView.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    if (dashboardAdapter == null) {
                                        dashboardAdapter = new DashboardAdapter(DashboardActivity.this, dashboardModelArrayList);
                                        allBooksRecyclerView.setAdapter(dashboardAdapter);
                                    } else {
                                        dashboardAdapter.notifyDataSetChanged();
                                    }
                                }
                            } else {
                                Toast.makeText(DashboardActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
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
                                Toast.makeText(DashboardActivity.this, message, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
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
    private void updateUI() {
        if (dashboardModelArrayList.isEmpty()) {
            noDataLayout.setVisibility(View.VISIBLE);
            allBooksRecyclerView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        } else {
            noDataLayout.setVisibility(View.GONE);
            allBooksRecyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            if (dashboardAdapter == null) {
                dashboardAdapter = new DashboardAdapter(this, dashboardModelArrayList);
                allBooksRecyclerView.setAdapter(dashboardAdapter);
            } else {
                dashboardAdapter.notifyDataSetChanged();
            }
        }
    }
    private String parseTags(JSONArray tagsArray) throws JSONException {
        StringBuilder tags = new StringBuilder();
        for (int j = 0; j < tagsArray.length(); j++) {
            tags.append(tagsArray.getString(j)).append(", ");
        }
        if (tags.length() > 0) {
            tags.setLength(tags.length() - 2); // Remove trailing comma and space
        }
        return tags.toString();
    }

    Dialog drawerDialog;
    LinearLayout layoutHome, layoutCart, layoutOrderHistory, layoutLogout, layoutShare, layoutAboutUs, layoutPrivacy, layoutTerms;
    TextView txtUsername, txtUserEmail;
    CircleImageView imgUser;
    MaterialCardView cardBack;

    private void showDrawerDialog() {
        drawerDialog = new Dialog(DashboardActivity.this);
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
                }
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
                        SessionManager sessionManager = new SessionManager(DashboardActivity.this);
                        sessionManager.logout();
                        Toast.makeText(DashboardActivity.this, "Logout Successfully...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DashboardActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(DashboardActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
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
}