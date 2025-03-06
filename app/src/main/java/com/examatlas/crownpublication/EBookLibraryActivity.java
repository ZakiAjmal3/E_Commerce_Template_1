package com.examatlas.crownpublication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.crownpublication.Adapter.DashboardAdapter;
import com.examatlas.crownpublication.Adapter.EBookLibraryAdapter;
import com.examatlas.crownpublication.Models.DashboardModel;
import com.examatlas.crownpublication.Models.extraModels.BookImageModels;
import com.examatlas.crownpublication.Utils.Constant;
import com.examatlas.crownpublication.Utils.MySingleton;
import com.examatlas.crownpublication.Utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EBookLibraryActivity extends AppCompatActivity {
    NestedScrollView scrollViewAboveBookRecycler;
    RecyclerView allBooksRecyclerView;
    private SearchView searchView;
    EBookLibraryAdapter dashboardAdapter;
    static ArrayList<DashboardModel> dashboardModelArrayList;
    ProgressBar progressBar, moreItemLoadProgressBar;
    RelativeLayout noDataLayout;
    ImageView backBtn, cartIconBtn;
    RelativeLayout topBar;
    SessionManager sessionManager;
    String authToken;
    private final String bookURL = Constant.BASE_URL + "my-ebook";
    private int currentPage = 1;
    private int totalPages = 1;
    private final int itemsPerPage = 10;
    TextView cartItemCountTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ebook_library);
        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");

        scrollViewAboveBookRecycler = findViewById(R.id.scrollViewAboveBookRecycler);
        scrollViewAboveBookRecycler.setVisibility(View.GONE);

        backBtn = findViewById(R.id.backBtn);
        cartIconBtn = findViewById(R.id.cartBtn);
        topBar = findViewById(R.id.topBar);

        searchView = findViewById(R.id.searchView);

        cartItemCountTxt = findViewById(R.id.cartItemCountTxt);
        setCartItemCountTxt();

        noDataLayout = findViewById(R.id.noDataLayout);
        progressBar = findViewById(R.id.progressBar);
        moreItemLoadProgressBar = findViewById(R.id.moreItemLoadProgressBar);
        allBooksRecyclerView = findViewById(R.id.booksRecycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 is the number of columns
        allBooksRecyclerView.setLayoutManager(gridLayoutManager);
        dashboardModelArrayList = new ArrayList<>();

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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        cartIconBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.IsLoggedIn()) {
                    Intent intent = new Intent(EBookLibraryActivity.this, CartViewActivity.class);
                    startActivity(intent);
                } else {
                    new MaterialAlertDialogBuilder(EBookLibraryActivity.this)
                            .setTitle("Login")
                            .setMessage("You need to login to view items in cart")
                            .setPositiveButton("Proceed to Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(EBookLibraryActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish(); // Finish the current activity
                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(EBookLibraryActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
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
                        Log.e("insideIf", "true");
                    }
                }
            }
        });
    }
    private void getAllBooks() {
        String subjectURLPage = bookURL  + "?pageNumber=" + currentPage + "&pageSize=" + itemsPerPage;
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
                                    String categoryId = jsonObject2.getString("categoryId");
                                    String  subCategoryId= jsonObject2.getString("subCategoryId");
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
                                            categoryId, null, subCategoryId, null,
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
                                    dashboardAdapter = new EBookLibraryAdapter(EBookLibraryActivity.this, dashboardModelArrayList);
                                    allBooksRecyclerView.setAdapter(dashboardAdapter);
                                }
                                moreItemLoadProgressBar.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(EBookLibraryActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                            Toast.makeText(EBookLibraryActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(EBookLibraryActivity.this, message, Toast.LENGTH_LONG).show();
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
