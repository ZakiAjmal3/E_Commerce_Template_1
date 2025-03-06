package com.examatlas.crownpublication;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
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
    RelativeLayout parentLayout;
    NestedScrollView scrollViewAboveBookRecycler;
    private ImageSlider slider;
    RecyclerView allBooksRecyclerView,examCategoryRecyclerView;
    ArrayList<DashboardCategoryModel> dashboardCategoryModelArrayList;
    ArrayList<SlideModel> sliderArrayList;
    private SearchView searchView;
    DashboardAdapter dashboardAdapter;
    static ArrayList<DashboardModel> dashboardModelArrayList;
    ArrayList<DashboardModel> dashboardSortingModelArrayList;
    ProgressBar progressBar,moreItemLoadProgressBar;
    RelativeLayout noDataLayout;
    ImageView imgMenu,cartIconBtn;
    RelativeLayout topBar;
    public String currentFrag = "HOME";
    SessionManager sessionManager;
    String authToken;
    private final String bookURL = Constant.BASE_URL + "books?type=book";
    private int currentPage = 1;
    private int totalPages = 1;
    private final int itemsPerPage = 10;
    TextView showAllCategoryBookTxt,noBookInThisCategoryTxt,cartItemCountTxt;
    private boolean isSearchViewFocused = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dasbboard);

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");

        parentLayout = findViewById(R.id.parentLayout);
        scrollViewAboveBookRecycler = findViewById(R.id.scrollViewAboveBookRecycler);
        scrollViewAboveBookRecycler.setVisibility(View.GONE);

        imgMenu = findViewById(R.id.imgMenu);
        cartIconBtn = findViewById(R.id.cartBtn);
        topBar = findViewById(R.id.topBar);
        slider = findViewById(R.id.slider);

        searchView = findViewById(R.id.searchView);

        noBookInThisCategoryTxt = findViewById(R.id.noBooksInThisCategoryTxt);
        showAllCategoryBookTxt = findViewById(R.id.showAllBookCategoryTxt);
        showAllCategoryBookTxt.setClickable(false);

        cartItemCountTxt = findViewById(R.id.cartItemCountTxt);
        setCartItemCountTxt();

        noDataLayout = findViewById(R.id.noDataLayout);
        progressBar = findViewById(R.id.progressBar);
        moreItemLoadProgressBar = findViewById(R.id.moreItemLoadProgressBar);
        allBooksRecyclerView = findViewById(R.id.booksRecycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 is the number of columns
        allBooksRecyclerView.setLayoutManager(gridLayoutManager);
        dashboardModelArrayList = new ArrayList<>();
        dashboardSortingModelArrayList = new ArrayList<>();

        dashboardCategoryModelArrayList = new ArrayList<>();

        examCategoryRecyclerView = findViewById(R.id.examCategory);
        examCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        sliderArrayList = new ArrayList<>();
        sliderArrayList.add(new SlideModel(R.drawable.image1, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image2, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image3, ScaleTypes.CENTER_CROP));
        sliderArrayList.add(new SlideModel(R.drawable.image4, ScaleTypes.CENTER_CROP));
        slider.setImageList(sliderArrayList);
//        getBannerImage();
        getCategory();

//        searchView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!TextUtils.isEmpty(searchView.getQuery())) {
//                    // When SearchView is clicked and it's empty, hide the whole layout
//                    hideLayout();
//                    openKeyboard();
//                } else {
//                    // If there's text in the SearchView, just open the keyboard
//                    openKeyboard();
//                }
//            }
//        });

        // Set up touch listener for the parent layout
        parentLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (searchView.isShown() && !isPointInsideView(event.getRawX(), event.getRawY(), searchView)) {
                        InputMethodManager imm = (InputMethodManager) DashboardActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0); // Hide the keyboard

                        // If the query is empty, show the whole layout again
                        if (TextUtils.isEmpty(searchView.getQuery())) {
                            showLayout();
                        }
                    }
                }
                return false; // Allow other events to be handled
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Not needed for this implementation
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Call the filter method when the search text changes
                if (dashboardAdapter != null) {
                    dashboardAdapter.filter(newText);
                }
                return true;
            }
        });

        showAllCategoryBookTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restoreOriginalList();
            }
        });

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
        scrollViewAboveBookRecycler.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // on scroll change we are checking when users scroll as bottom.
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // in this method we are incrementing page number,
                    // making progress bar visible and calling get data method.
                    currentPage++;
                    int scrollThreshold = 50; // threshold to trigger load more data
                    int diff = (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) - scrollY;
                    Log.e("ScrollDebug", "diff: " + diff);
                    // Check if we have scrolled to the bottom or near bottom
                    if (diff <= scrollThreshold && currentPage <= totalPages) {
                        moreItemLoadProgressBar.setVisibility(View.VISIBLE);
                        // on below line we are again calling
                        // a method to load data in our array list.
                        getAllBooks();
                        Log.e("insideIf","true");
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(searchView.getQuery())) {
            // If no query is present, show the layout again
            showLayout();
            super.onBackPressed();
        } else {
            // If there's text, just close the keyboard and keep the layout hidden
            InputMethodManager imm = (InputMethodManager) DashboardActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0); // Hide the keyboard
        }
    }

    // Helper methods to control layout visibility
    private void hideLayout() {
        scrollViewAboveBookRecycler.setVisibility(View.GONE);
    }

    private void showLayout() {
        scrollViewAboveBookRecycler.setVisibility(View.VISIBLE);
    }

    private void openKeyboard() {
        searchView.setIconified(false); // Expands the search view
        searchView.requestFocus(); // Requests focus
        InputMethodManager imm = (InputMethodManager) DashboardActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT); // Show the keyboard
    }

    private boolean isPointInsideView(float x, float y, View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return (x >= location[0] && x <= (location[0] + view.getWidth()) &&
                y >= location[1] && y <= (location[1] + view.getHeight()));
    }

//    private void getBannerImage() {
//        String bannerImageURL = Constant.BASE_URL + "banner/get-banner";
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, bannerImageURL, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            String status = response.getString("status");
//                            JSONObject jsonObject1 = response.getJSONObject("data");
//                            String dataID = jsonObject1.getString("_id");
//                            JSONArray jsonArray1 = jsonObject1.getJSONArray("images");
//                            for (int i = 0; i < jsonArray1.length(); i++) {
//                                JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
//                                String imageUrl = jsonObject2.getString("url");
//                                sliderArrayList.add(new SlideModel(imageUrl, ScaleTypes.FIT));
//                            }
//                            slider.setImageList(sliderArrayList);
//                        } catch (JSONException e) {
//                            Log.e("Exam Catch error", "Error parsing JSON: " + e.getMessage());
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        String errorMessage = "Error: " + error.toString();
//                        if (error.networkResponse != null) {
//                            try {
//                                // Parse the error response
//                                String jsonError = new String(error.networkResponse.data);
//                                JSONObject jsonObject = new JSONObject(jsonError);
//                                String message = jsonObject.optString("message", "Unknown error");
//                                // Now you can use the message
//                                Toast.makeText(DashboardActivity.this, message, Toast.LENGTH_LONG).show();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        Log.e("ExamListError", errorMessage);
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                headers.put("Authorization", "Bearer " + authToken);
//                return headers;
//            }
//        };
//        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
//    }

    private void getCategory() {
        String examCategoryURL = Constant.BASE_URL + "category?pageNumber=1&pageSize=100";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, examCategoryURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                                String categoryId = jsonObject2.getString("_id");
                                String categoryName = jsonObject2.getString("categoryName");
                                String slug = jsonObject2.getString("slug");
                                String isActive = jsonObject2.getString("isActive");
                                DashboardCategoryModel dashboardCategoryModel = new DashboardCategoryModel(categoryId,categoryName,slug,isActive);
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
        String subjectURLPage = bookURL  + "&pageNumber=" + currentPage + "&pageSize=" + itemsPerPage;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, subjectURLPage, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("success");

                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("data");

                                totalPages = response.getInt("totalPage");

                                // Parse books directly here
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);

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
                                    JSONObject categoryObject = jsonObject2.getJSONObject("category");
                                    String categoryId = categoryObject.getString("_id");
                                    String categoryName = categoryObject.getString("categoryName");
                                    JSONObject subCategoryObject = jsonObject2.getJSONObject("subCategory");
                                    String subCategoryId = subCategoryObject.getString("_id");
                                    String subCategoryName = subCategoryObject.getString("name");
                                    DashboardModel model = new DashboardModel(
                                            jsonObject2.getString("_id"),
                                            jsonObject2.getString("title"),
                                            jsonObject2.getString("sku"),
                                            jsonObject2.getString("slug"),
                                            jsonObject2.getString("publication"),
                                            jsonObject2.getString("stock"),
                                            jsonObject2.getString("price"),
                                            jsonObject2.getString("sellingPrice"),
                                            jsonObject2.getString("description"),
                                            jsonObject2.getString("author"),
                                            jsonObject2.getString("isActive"),
                                            jsonObject2.getString("language"),
                                            jsonObject2.getString("edition"),
                                            categoryId, categoryName, subCategoryId, subCategoryName,
                                            parseTags(jsonObject2.getJSONArray("tags")),
                                            bookImageArrayList);

                                    boolean isPresent = false;
                                    for (int j = 0; j < dashboardModelArrayList.size(); j++) {
                                        if (dashboardModelArrayList.get(j).getBookId().equals(model.getBookId())) {
                                            isPresent = true;
                                            break;
                                        }
                                    }
                                    if (!isPresent) {
                                        dashboardModelArrayList.add(model);
                                    }
                                    // Avoid adding duplicate categories
                                }
                                // Update UI if categories exist
                                if (dashboardModelArrayList.isEmpty()) {
                                    noDataLayout.setVisibility(View.VISIBLE);
                                    searchView.setVisibility(View.GONE);
                                    allBooksRecyclerView.setVisibility(View.GONE);
                                    progressBar.setVisibility(View.GONE);
                                    moreItemLoadProgressBar.setVisibility(View.GONE);
                                } else {
                                    allBooksRecyclerView.setVisibility(View.VISIBLE);
                                    scrollViewAboveBookRecycler.setVisibility(View.VISIBLE);
                                    searchView.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                    moreItemLoadProgressBar.setVisibility(View.GONE);
                                    dashboardAdapter = new DashboardAdapter(DashboardActivity.this, dashboardModelArrayList);
                                    allBooksRecyclerView.setAdapter(dashboardAdapter);
                                }
                                moreItemLoadProgressBar.setVisibility(View.GONE);
                                showAllCategoryBookTxt.setClickable(true);
                            } else {
                                Toast.makeText(DashboardActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                            Toast.makeText(DashboardActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
    LinearLayout layoutHome, layoutCart, layoutOrderHistory,layoutProfile,layoutOrderInBulk,
            layoutJoinAsAuthor, layoutLogout,layoutLogin, layoutShare, layoutAboutUs, layoutPrivacy,
            layoutTerms,layoutEBook,layoutLibrary;
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
        layoutOrderInBulk = drawerDialog.findViewById(R.id.layoutBulkOrder);
        layoutOrderInBulk.setVisibility(View.GONE);
        layoutJoinAsAuthor = drawerDialog.findViewById(R.id.layoutJoinAsAuthor);
        layoutJoinAsAuthor.setVisibility(View.GONE);
        layoutLogout = drawerDialog.findViewById(R.id.layoutLogout);
        layoutLogin = drawerDialog.findViewById(R.id.layoutLogin);
        layoutShare = drawerDialog.findViewById(R.id.layoutShare);
        layoutAboutUs = drawerDialog.findViewById(R.id.layoutAboutUs);
        layoutPrivacy = drawerDialog.findViewById(R.id.layoutPrivacy);
        layoutTerms = drawerDialog.findViewById(R.id.layoutTerms);
        layoutEBook = drawerDialog.findViewById(R.id.layoutEBook);
        layoutLibrary = drawerDialog.findViewById(R.id.layoutLibrary);
        txtUsername = drawerDialog.findViewById(R.id.txtUsername);
        txtUserEmail = drawerDialog.findViewById(R.id.txtUserEmail);
        cardBack = drawerDialog.findViewById(R.id.cardBack);
        imgUser = drawerDialog.findViewById(R.id.imgUser);

        txtUsername.setText(sessionManager.getUserData().get("firstName") + " " + sessionManager.getUserData().get("lastName"));
        txtUserEmail.setText(sessionManager.getUserData().get("email"));

        if (sessionManager.IsLoggedIn()){
            layoutLibrary.setVisibility(View.VISIBLE);
            layoutCart.setVisibility(View.VISIBLE);
            layoutOrderHistory.setVisibility(View.VISIBLE);
            layoutProfile.setVisibility(View.VISIBLE);
            layoutLogin.setVisibility(View.GONE);
            layoutLogout.setVisibility(View.VISIBLE);
        }else {
            layoutLibrary.setVisibility(View.GONE);
            layoutCart.setVisibility(View.GONE);
            layoutOrderHistory.setVisibility(View.GONE);
            layoutProfile.setVisibility(View.GONE);
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
                    Intent intent = new Intent(DashboardActivity.this, OrderHistoryActivity.class);
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
        layoutOrderInBulk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, BulkOrderRegisterActivity.class);
                startActivity(intent);
            }
        });
        layoutJoinAsAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, JoinAsAuthorActivity.class);
                startActivity(intent);
            }
        });
        layoutEBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, EBookHomepageActivity.class);
                startActivity(intent);
            }
        });
        layoutLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, EBookLibraryActivity.class);
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

    public void sortBookWithExamName(String examName) {
        // Clear the sorting list before adding new items
        dashboardSortingModelArrayList.clear();

        if (!examName.isEmpty()) {
            // Loop through the list and add matching categories to the sorted list
            for (int i = 0; i < dashboardModelArrayList.size(); i++) {
                if (dashboardModelArrayList.get(i).getCategoryName().equalsIgnoreCase(examName)) {
                    dashboardSortingModelArrayList.add(dashboardModelArrayList.get(i));
                }
            }

            // Check if sorting list is empty and update the UI accordingly
            if (dashboardSortingModelArrayList.isEmpty()) {
                noBookInThisCategoryTxt.setVisibility(View.VISIBLE);
                allBooksRecyclerView.setVisibility(View.GONE);
            } else {
                // Update adapter data and notify the change
                dashboardAdapter.updateData(dashboardSortingModelArrayList); // Use a method to update data in the adapter
                allBooksRecyclerView.setVisibility(View.VISIBLE);
                noBookInThisCategoryTxt.setVisibility(View.GONE);
            }
        } else {
            // If the exam name is empty, show all books (restore to the full list)
            restoreOriginalList();
        }
    }
    public void restoreOriginalList() {
        // Update adapter to show the original list
        dashboardAdapter.updateData(dashboardModelArrayList);
        allBooksRecyclerView.setVisibility(View.VISIBLE);
        noBookInThisCategoryTxt.setVisibility(View.GONE); // Hide the "No books" message if visible
    }

    public static ArrayList<DashboardModel> getDashboardModelArrayList() {
        return dashboardModelArrayList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllBooks();
        if (dashboardAdapter != null) {
            dashboardAdapter.notifyDataSetChanged();
        }
        setCartItemCountTxt();
    }
    public void setCartItemCountTxt(){
        String quantity = sessionManager.getCartQuantity();
        if (!quantity.equals("0")) {
            cartItemCountTxt.setVisibility(View.VISIBLE);
            cartItemCountTxt.setText(quantity);
        }else {
            cartItemCountTxt.setVisibility(View.GONE);
        }
    }
}