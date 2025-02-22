package com.examatlas.crownpublication;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.crownpublication.Adapter.DashboardAdapter;
import com.examatlas.crownpublication.Adapter.extraAdapter.BookImagePreviewAdapter;
import com.examatlas.crownpublication.Models.DashboardModel;
import com.examatlas.crownpublication.Utils.Constant;
import com.examatlas.crownpublication.Utils.MySingleton;
import com.examatlas.crownpublication.Utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProductDescriptionActivity extends AppCompatActivity {
    ImageView backBtn;
    ViewPager2 bookImageView;
    LinearLayout dotLayout;
    TextView bookTitle,bookPrice,category,author,tags;
    WebView webView;
    Button addToCartBtn,goToCartBtn, butNowBtn;
    String bookImageUrl, bookIdStr,titleStr,priceStr,sellPriceStr,categoryStr,authorStr,tagsStr,contentStr;
    SessionManager sessionManager;
    String authToken;
    ArrayList<String> bookImageURLArrayList;
    ArrayList<DashboardModel> dashboardModelArrayList;
    ArrayList<DashboardModel> relatedWithCategoryDashboardModelArrayList;
    RecyclerView relatedBookRecyclerView;
    DashboardAdapter dashboardAdapter;
    DashboardModel dashboardModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        bookImageView = findViewById(R.id.imgBook);
        dotLayout = findViewById(R.id.indicatorLayout);
        backBtn = findViewById(R.id.backBtn);
        bookTitle = findViewById(R.id.bookTitle);
        bookPrice = findViewById(R.id.bookPriceInfo);
        category = findViewById(R.id.categoryTxtDisplay);
        author = findViewById(R.id.authorTxtDisplay);
        tags = findViewById(R.id.tagsTxtDisplay);
        webView = findViewById(R.id.webView);
        addToCartBtn = findViewById(R.id.addToCartBtn);
        goToCartBtn = findViewById(R.id.goToCartBtn);
        butNowBtn = findViewById(R.id.buyNow);

        relatedBookRecyclerView = findViewById(R.id.relatedBooksRecyclerView);
        relatedBookRecyclerView.setLayoutManager(new GridLayoutManager(this,2));

        bookImageURLArrayList = new ArrayList<>();

        Intent intent = getIntent();
        bookIdStr = intent.getStringExtra("bookId");
        titleStr = intent.getStringExtra("bookTitle");
        priceStr = intent.getStringExtra("bookPrice");
        sellPriceStr = intent.getStringExtra("bookSellPrice");
        categoryStr = intent.getStringExtra("bookCategory");
        authorStr = intent.getStringExtra("bookAuthor");
        tagsStr = intent.getStringExtra("bookTags");
        contentStr = intent.getStringExtra("bookContent");
        bookImageURLArrayList = intent.getStringArrayListExtra("bookImage");

        sessionManager = new SessionManager(this);
        authToken = sessionManager.getUserData().get("authToken");

        bookTitle.setText(titleStr);
        category.setText(categoryStr);
        tags.setText(tagsStr);
        author.setText(authorStr);

        BookImagePreviewAdapter bookImageAdapter = new BookImagePreviewAdapter(bookImageURLArrayList,bookImageView,dotLayout);
        bookImageView.setAdapter(bookImageAdapter);
        // Calculate prices and discount
        String purchasingPrice = sellPriceStr;
        String originalPrice = priceStr;
        int discount = Integer.parseInt(purchasingPrice) * 100 / Integer.parseInt(originalPrice);
        discount = 100 - discount;

        // Create a SpannableString for the original price with strikethrough
        SpannableString spannableOriginalPrice = new SpannableString("₹" + originalPrice);
        spannableOriginalPrice.setSpan(new StrikethroughSpan(), 0, spannableOriginalPrice.length(), 0);

        // Create the discount text
        String discountText = "(-" + discount + "%)";
        SpannableStringBuilder spannableText = new SpannableStringBuilder();
        spannableText.append("₹" + purchasingPrice + " ");
        spannableText.append(spannableOriginalPrice);
        spannableText.append(" " + discountText);

        // Set the color for the discount percentage
        int startIndex = spannableText.length() - discountText.length();
        spannableText.setSpan(new ForegroundColorSpan(Color.GREEN), startIndex, spannableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        bookPrice.setText(spannableText);

//         Enable JavaScript (optional, depending on your content)
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Inject CSS to control the image size
        String injectedCss = "<style>"
                + "p { font-size: 20px; }" // Increase text size only for <p> tags (paragraphs)
                + "img { width: 100%; height: auto; }" // Adjust image size as needed
                + "</style>";
        String fullHtmlContent = injectedCss + contentStr;

        // Disable scrolling and over-scrolling
        webView.setVerticalScrollBarEnabled(false);  // Disable vertical scroll bar
        webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER); // Disable over-scrolling effect

        // Load the modified HTML content
        webView.loadData(fullHtmlContent, "text/html", "UTF-8");

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.IsLoggedIn()) {
                    addToCartFunction();
                } else {
                    new MaterialAlertDialogBuilder(ProductDescriptionActivity.this)
                            .setTitle("Login")
                            .setMessage("You need to login to add items to cart")
                            .setPositiveButton("Proceed to Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(ProductDescriptionActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(ProductDescriptionActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                }
            }
        });

        goToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ProductDescriptionActivity.this,CartViewActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        butNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCartFunction();
                Intent intent1 = new Intent(ProductDescriptionActivity.this,CartViewActivity.class);
                startActivity(intent1);
                finish();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        bookImageView.setClickable(true);  // Ensure it's clickable

        bookImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                    openImageZoomOutDialog();  // Open the dialog on tap
                    return true;  // Indicate that the touch has been handled
                }
                return false;  // Allow ViewPager2 to handle other touch events
            }
        });

        relatedWithCategoryDashboardModelArrayList = new ArrayList<>();
        dashboardModelArrayList = DashboardActivity.getDashboardModelArrayList();
        if (!dashboardModelArrayList.isEmpty()) {
            for (int i = 0; i < dashboardModelArrayList.size(); i++) {
                String categoryStrComparing;
                categoryStrComparing = dashboardModelArrayList.get(i).getCategoryName();
                if (categoryStrComparing.equalsIgnoreCase(categoryStr)) {
                    relatedWithCategoryDashboardModelArrayList.add(dashboardModelArrayList.get(i));
                }
            }
        }
        if (!relatedWithCategoryDashboardModelArrayList.isEmpty()) {
            dashboardAdapter = new DashboardAdapter(ProductDescriptionActivity.this, relatedWithCategoryDashboardModelArrayList);
            relatedBookRecyclerView.setAdapter(dashboardAdapter);
        }
    }
    Dialog drawerDialog;
    ImageView crossBtn;
    ViewPager2 bookImageViewPager;
    LinearLayout dotDialogLinearLayout;
    // Method to open the image zoom-out dialog
    private void openImageZoomOutDialog() {
        drawerDialog = new Dialog(ProductDescriptionActivity.this);
        drawerDialog.setContentView(R.layout.book_image_zoom_out_layout);
        drawerDialog.setCancelable(true);

        crossBtn = drawerDialog.findViewById(R.id.crossBtn);
        bookImageViewPager = drawerDialog.findViewById(R.id.viewPager);
        dotDialogLinearLayout = drawerDialog.findViewById(R.id.indicatorLayout);

        BookImagePreviewAdapter bookImageAdapter = new BookImagePreviewAdapter(bookImageURLArrayList, bookImageViewPager, dotDialogLinearLayout);
        bookImageViewPager.setAdapter(bookImageAdapter);

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerDialog.dismiss();
                // Re-enable swipe functionality after dialog is dismissed
                bookImageView.setUserInputEnabled(true);
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

    private void addToCartFunction() {
        String userId = sessionManager.getUserData().get("user_id");
        String addToCartUrl = Constant.BASE_URL + "cart/add";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId", userId);
            jsonObject.put("bookId", bookIdStr);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addToCartUrl, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.getString("success");
                            String message = response.getString("message");
                            Toast.makeText(ProductDescriptionActivity.this, message, Toast.LENGTH_SHORT).show();
                            addToCartBtn.setVisibility(View.GONE);
                            goToCartBtn.setVisibility(View.VISIBLE);

                        } catch (JSONException e) {
                            Toast.makeText(ProductDescriptionActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        String responseData = new String(error.networkResponse.data, "UTF-8");
                        errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
                        errorMessage += "\nResponse Data: " + responseData;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(ProductDescriptionActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                Log.e("LoginActivity", errorMessage);
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
        MySingleton.getInstance(ProductDescriptionActivity.this).addToRequestQueue(jsonObjectRequest);
    }
}