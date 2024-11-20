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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.examatlas.crownpublication.Adapter.DashboardCategoryAdapter;
import com.examatlas.crownpublication.Models.DashboardCategoryModel;
import com.examatlas.crownpublication.Models.DashboardModel;
import com.examatlas.crownpublication.Models.extraModels.BookImageModels;
import com.examatlas.crownpublication.Utils.Constant;
import com.examatlas.crownpublication.Utils.MySingleton;
import com.examatlas.crownpublication.Utils.SessionManager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity {
    private ImageSlider slider;
    RecyclerView allBooksRecyclerView,examCategoryRecyclerView;
    ArrayList<DashboardCategoryModel> dashboardCategoryModelArrayList;
    ArrayList<SlideModel> sliderArrayList;
    DashboardModel dashboardModel;
    DashboardAdapter dashboardAdapter;
    ArrayList<DashboardModel> dashboardModelArrayList;
    ArrayList<DashboardModel> dashboardSortingModelArrayList;
    ProgressBar progressBar;
    RelativeLayout noDataLayout;
    ImageView imgMenu,cartIconBtn;
    RelativeLayout topBar;
    public String currentFrag = "HOME";
    SessionManager sessionManager;
    String authToken;
    private final String bookURL = Constant.BASE_URL + "book/getAllBooks";
    private int currentPage = 1;
    private int totalPages = 1;
    private final int itemsPerPage = 10;
    private boolean isLoading = false;
    TextView showAllCategoryBookTxt,noBookInThisCategoryTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dasbboard);

        imgMenu = findViewById(R.id.imgMenu);
        cartIconBtn = findViewById(R.id.cartBtn);
        topBar = findViewById(R.id.topBar);
        slider = findViewById(R.id.slider);

        noBookInThisCategoryTxt = findViewById(R.id.noBooksInThisCategoryTxt);
        showAllCategoryBookTxt = findViewById(R.id.showAllBookCategoryTxt);

        showAllCategoryBookTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllBooks();
            }
        });

        noDataLayout = findViewById(R.id.noDataLayout);
        progressBar = findViewById(R.id.progressBar);
        allBooksRecyclerView = findViewById(R.id.booksRecycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 is the number of columns
        allBooksRecyclerView.setLayoutManager(gridLayoutManager);
        dashboardModelArrayList = new ArrayList<>();
        dashboardSortingModelArrayList = new ArrayList<>();

        dashboardCategoryModelArrayList = new ArrayList<>();

        examCategoryRecyclerView = findViewById(R.id.examCategory);
        examCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");

        sliderArrayList = new ArrayList<>();
//        sliderArrayList.add(new SlideModel(R.drawable.image1, ScaleTypes.CENTER_CROP));
//        sliderArrayList.add(new SlideModel(R.drawable.image2, ScaleTypes.CENTER_CROP));
//        sliderArrayList.add(new SlideModel(R.drawable.image3, ScaleTypes.CENTER_CROP));
//        slider.setImageList(sliderArrayList);

        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDrawerDialog();
            }
        });
        cartIconBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.IsLoggedIn()) {
                    Intent intent = new Intent(DashboardActivity.this, CartViewActivity.class);
                    startActivity(intent);
                }else {
                    new MaterialAlertDialogBuilder(DashboardActivity.this)
                            .setTitle("Login")
                            .setMessage("You need to login to view items in cart")
                            .setPositiveButton("Proceed to Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish(); // Finish the current activity
                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(DashboardActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                }
            }
        });
        getBannerImage();
        getCategory();
        getAllBooks();
        allBooksRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Get the GridLayoutManager and find the last visible item position
                int lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = gridLayoutManager.getItemCount();

                Log.d("ScrollListener", "Last visible item position: " + lastVisibleItemPosition + " Total items: " + totalItemCount);

                // Check if we are at the bottom of the list
                if (lastVisibleItemPosition + 1 >= totalItemCount && !isLoading) {
                    // Check if there are more pages to load
                    if (currentPage < totalPages) {
                        currentPage++;  // Increment the current page
                        getAllBooks();   // Fetch the next set of books
                    }
                }
            }
        });

    }

    private void getBannerImage() {
        String bannerImageURL = Constant.BASE_URL + "banner/get-banner";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, bannerImageURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");
                            JSONObject jsonObject1 = response.getJSONObject("data");
                            String dataID = jsonObject1.getString("_id");
                            JSONArray jsonArray1 = jsonObject1.getJSONArray("images");
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                                String imageUrl = jsonObject2.getString("url");
                                sliderArrayList.add(new SlideModel(imageUrl, ScaleTypes.FIT));
                            }
                            slider.setImageList(sliderArrayList);
                        } catch (JSONException e) {
                            Log.e("Exam Catch error", "Error parsing JSON: " + e.getMessage());
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
                        Log.e("ExamListError", errorMessage);
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

    private void getCategory() {
        String examCategoryURL = Constant.BASE_URL + "category/getCategory";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, examCategoryURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("status");

                            JSONObject jsonObject1 = response.getJSONObject("pagination");
                            String totalRows = jsonObject1.getString("totalRows");
                            String totalPages = jsonObject1.getString("totalPages");
                            String currentPage = jsonObject1.getString("currentPage");

                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                String categoryId = jsonObject2.getString("_id");
                                String categoryName = jsonObject2.getString("categoryName");
                                String categoryDescription = jsonObject2.getString("description");
                                String tags =  parseTags(jsonObject2.getJSONArray("tags"));
                                String isActive = jsonObject2.getString("is_active");
                                DashboardCategoryModel dashboardCategoryModel = new DashboardCategoryModel(categoryId,categoryName,categoryDescription,tags,isActive,totalRows,totalPages,currentPage);
                                dashboardCategoryModelArrayList.add(dashboardCategoryModel);
                            }
                            examCategoryRecyclerView.setAdapter(new DashboardCategoryAdapter(dashboardCategoryModelArrayList,DashboardActivity.this));
                            getAllBooks();
                        } catch (JSONException e) {
                            Log.e("Exam Catch error", "Error parsing JSON: " + e.getMessage());
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
                        Log.e("ExamListError", errorMessage);
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

    private void getAllBooks() {
        if (isLoading) return;  // Prevent making requests if one is already in progress
        isLoading = true;  // Set flag to true when the request is in progress
//        String paginatedURL = bookURL + "?type=book&page=" + currentPage + "&per_page=" + itemsPerPage;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, bookURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            allBooksRecyclerView.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            boolean status = response.getBoolean("status");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("books");
                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

//                                    JSONObject jsonObject = response.getJSONObject("pagination");
//                                    int totalRows = Integer.parseInt(jsonObject.getString("totalRows"));
                                    int totalRows = 10;
//                                    totalPages = Integer.parseInt(jsonObject.getString("totalPages"));
                                    totalPages = 10;
//                                    currentPage = Integer.parseInt(jsonObject.getString("currentPage"));
                                    currentPage = 1;
//                                    int pageSize = Integer.parseInt(jsonObject.getString("pageSize"));
                                    int pageSize = 10;

                                    ArrayList<BookImageModels> bookImageArrayList = new ArrayList<>();
                                    JSONArray jsonArray1 = jsonObject2.getJSONArray("images");
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
                                    JSONObject jsonObject3 = jsonObject2.getJSONObject("dimension");
                                    String length = jsonObject3.getString("length");
                                    String breadth = jsonObject3.getString("breadth");
                                    String height = jsonObject3.getString("height");
                                    DashboardModel model = new DashboardModel(
                                            jsonObject2.getString("_id"),
                                            jsonObject2.getString("title"),
                                            jsonObject2.getString("keyword"),
                                            jsonObject2.getString("price"),
                                            jsonObject2.getString("sellPrice"),
                                            jsonObject2.getString("author"),
                                            jsonObject2.getString("category"),
                                            jsonObject2.getString("content"),
                                            jsonObject2.getString("subject"),
                                            length,breadth,height,
                                            jsonObject2.getString("weight"),
                                            jsonObject2.getString("isbn"),
                                            parseTags(jsonObject2.getJSONArray("tags")),
                                            jsonObject2.getString("IsInCart"),// Ensure this method is implemented correctly,
                                            bookImageArrayList,
                                            jsonObject2.getString("createdAt"),
                                            jsonObject2.getString("updatedAt"),
                                            "false",
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
    LinearLayout layoutHome, layoutCart, layoutOrderHistory,layoutProfile, layoutLogout,layoutLogin, layoutShare, layoutAboutUs, layoutPrivacy, layoutTerms;
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
        layoutProfile = drawerDialog.findViewById(R.id.layoutProfile);
        layoutLogout = drawerDialog.findViewById(R.id.layoutLogout);
        layoutLogin = drawerDialog.findViewById(R.id.layoutLogin);
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

        if (sessionManager.IsLoggedIn()){
            layoutLogin.setVisibility(View.GONE);
            layoutLogout.setVisibility(View.VISIBLE);
        }else {
            layoutLogout.setVisibility(View.GONE);
            layoutLogin.setVisibility(View.VISIBLE);
        }

        layoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                if (!currentFrag.equals("HOME")) {
                    currentFrag = "HOME";
                    Intent intent = new Intent(DashboardActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        layoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                if (sessionManager.IsLoggedIn()) {
                    Intent intent = new Intent(DashboardActivity.this, CartViewActivity.class);
                    startActivity(intent);
                }else {
                        new MaterialAlertDialogBuilder(DashboardActivity.this)
                                .setTitle("Login")
                                .setMessage("You need to login to view items in cart")
                                .setPositiveButton("Proceed to Login", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(DashboardActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
                }
            }
        });
        layoutOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.IsLoggedIn()) {
                    Intent intent = new Intent(DashboardActivity.this, CartViewActivity.class);
                    startActivity(intent);
                    drawerDialog.dismiss();
                }else {
                    new MaterialAlertDialogBuilder(DashboardActivity.this)
                            .setTitle("Login")
                            .setMessage("You need to login to view order history")
                            .setPositiveButton("Proceed to Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish(); // Finish the current activity
                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(DashboardActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                }
            }
        });
        layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.IsLoggedIn()) {
                    Intent intent = new Intent(DashboardActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    drawerDialog.dismiss();
                }else {
                    new MaterialAlertDialogBuilder(DashboardActivity.this)
                            .setTitle("Login")
                            .setMessage("You need to login to view profile")
                            .setPositiveButton("Proceed to Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish(); // Finish the current activity
                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(DashboardActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
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

        layoutAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
        });
        layoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        layoutPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, PrivacyPolicyActivity.class);
                startActivity(intent);
            }
        });
        layoutTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, TermsAndConditionActivity.class);
                startActivity(intent);
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

    public void sortBookWithExamName(String examName){
        if (!examName.isEmpty()){
            for (int i = 0; i < dashboardModelArrayList.size(); i++) {
                if (dashboardModelArrayList.get(i).getCategory().equalsIgnoreCase(examName)){
                    dashboardSortingModelArrayList.add(dashboardModelArrayList.get(i));
                }
            }
            if (dashboardSortingModelArrayList.isEmpty()){
                noBookInThisCategoryTxt.setVisibility(View.VISIBLE);
                allBooksRecyclerView.setVisibility(View.GONE);
            }else {
                dashboardAdapter = new DashboardAdapter(DashboardActivity.this, dashboardSortingModelArrayList);
                allBooksRecyclerView.setAdapter(dashboardAdapter);
            }
        }else {
            dashboardAdapter = new DashboardAdapter(DashboardActivity.this, dashboardModelArrayList);
            allBooksRecyclerView.setAdapter(dashboardAdapter);
        }
        dashboardAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllBooks();
        if (dashboardAdapter != null) {
            dashboardAdapter.notifyDataSetChanged();
        }
    }
}