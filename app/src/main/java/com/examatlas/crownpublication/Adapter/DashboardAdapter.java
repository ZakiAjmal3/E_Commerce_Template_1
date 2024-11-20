package com.examatlas.crownpublication.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.crownpublication.Adapter.extraAdapter.BookImageAdapter;
import com.examatlas.crownpublication.CartViewActivity;
import com.examatlas.crownpublication.LoginActivity;
import com.examatlas.crownpublication.Models.DashboardModel;
import com.examatlas.crownpublication.Models.extraModels.BookImageModels;
import com.examatlas.crownpublication.ProductDescriptionActivity;
import com.examatlas.crownpublication.R;
import com.examatlas.crownpublication.Utils.Constant;
import com.examatlas.crownpublication.Utils.MySingleton;
import com.examatlas.crownpublication.Utils.SessionManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<DashboardModel> hardBookECommPurchaseModelArrayList;
    private final ArrayList<DashboardModel> originalHardBookECommPurchaseModelArrayList;
    private final ArrayList<Boolean> heartToggleStates; // List to track heart states
    private String currentQuery = "";
    String authToken;
    SessionManager sessionManager;
    ArrayList bookImageUrls;

    public DashboardAdapter(Context context, ArrayList<DashboardModel> hardBookECommPurchaseModelArrayList) {
        this.originalHardBookECommPurchaseModelArrayList = new ArrayList<>(hardBookECommPurchaseModelArrayList);
        this.hardBookECommPurchaseModelArrayList = new ArrayList<>(originalHardBookECommPurchaseModelArrayList);
        this.context = context;
        this.heartToggleStates = new ArrayList<>(Collections.nCopies(hardBookECommPurchaseModelArrayList.size(), false));
        sessionManager = new SessionManager(context);
        authToken = sessionManager.getUserData().get("authToken");
        bookImageUrls = new ArrayList<>();
    }

    @NonNull
    @Override
    public DashboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_books_dashboard_recycler_item_list, parent, false);
        return new DashboardAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DashboardModel currentBook = hardBookECommPurchaseModelArrayList.get(position);
        holder.itemView.setTag(currentBook);

        // Calculate prices and discount
        String purchasingPrice = currentBook.getSellPrice();
        String originalPrice = currentBook.getPrice();
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

        String title = currentBook.getTitle();
        // Set text to holder
        holder.setHighlightedText(holder.title, title, currentQuery);

// Measure the number of lines after the text has been set
        holder.title.post(new Runnable() {
            @Override
            public void run() {
                // Check the actual number of lines used by the TextView
                if (holder.title.getLineCount() > 1) {
                    // Truncate the title and append ellipsis if it takes more than one line
                    String truncatedTitle = title;
                    if (title.length() > 50) {
                        truncatedTitle = title.substring(0, 50) + "...";
                    }
                    // Set the truncated text
                    holder.title.setText(truncatedTitle);
                }
            }
        });

        holder.setHighlightedText(holder.author, currentBook.getAuthor(), currentQuery);
        holder.setHighlightedText(holder.category, currentBook.getCategory(), currentQuery);
        holder.setHighlightedPrice(holder.price, spannableText, currentQuery);

        if (currentBook.getIsInCart().equals("true")){
            holder.addToCartBtn.setVisibility(View.GONE);
            holder.goToCartBtn.setVisibility(View.VISIBLE);
        }

        BookImageAdapter bookImageAdapter = new BookImageAdapter(currentBook.getImages(),holder.viewPager,holder.dotsLinearLayout);
        holder.viewPager.setAdapter(bookImageAdapter);

        // Set the heart icon based on the state
        holder.toggleHeartIcon.setImageResource(heartToggleStates.get(position) ? R.drawable.ic_heart_red : R.drawable.ic_heart_white);

        // Heart icon toggle logic
        holder.toggleHeartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isHearted = heartToggleStates.get(position); // Get the current state

                if (!isHearted) {
                    addToWishlist(currentBook,holder.toggleHeartIcon);
                    holder.toggleHeartIcon.setImageResource(R.drawable.ic_heart_red);
                    heartToggleStates.set(position, true); // Update the state to true
                } else {
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("WishList")
                            .setMessage("Are you sure you want to remove this item from wishlist?")
                            .setPositiveButton("REMOVE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    holder.toggleHeartIcon.setImageResource(R.drawable.ic_heart_white);
                                    removeItemFromWishlist(currentBook);
                                    heartToggleStates.set(position, false); // Update the state to false
                                }
                            })
                            .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Do nothing, keep the current state
                                }
                            }).show();
                }
            }
        });

        holder.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sessionManager.IsLoggedIn()) {

                    String userId = sessionManager.getUserData().get("user_id");
                    String bookID = currentBook.getBookID();
                    String addToCartUrl = Constant.BASE_URL + "cart/add";

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("userId", userId);
                        jsonObject.put("bookId", bookID);
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
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                        holder.addToCartBtn.setVisibility(View.GONE);
                                        holder.goToCartBtn.setVisibility(View.VISIBLE);
                                    } catch (JSONException e) {
                                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
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
                    MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
                }
                else {
                    new MaterialAlertDialogBuilder(context)
                            .setTitle("Login")
                            .setMessage("You need to login to add items to cart")
                            .setPositiveButton("Proceed to Login", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(context, LoginActivity.class);
                                    context.startActivity(intent);
                                    if (context instanceof Activity) {
                                        ((Activity) context).finish(); // Finish the current activity
                                    }
                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            }).show();
                }
            }
        });
        holder.goToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CartViewActivity.class);
                context.startActivity(intent);
            }
        });
    }
    private void addToWishlist(DashboardModel currentBook, ImageView toggleHeartIcon) {

        if (sessionManager.IsLoggedIn()) {

            String addBookUrl = Constant.BASE_URL + "wishlist/toggleWishlist";
            String userID = sessionManager.getUserData().get("user_id");
            String bookID = currentBook.getBookID();
            JSONObject bookDetails = new JSONObject();
            try {
                bookDetails.put("userId", userID);
                bookDetails.put("bookId", bookID);
            } catch (JSONException e) {
                Log.e("JSON_ERROR", "Error creating JSON object: " + e.getMessage());
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, addBookUrl, bookDetails,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                boolean status = response.getBoolean("status");
                                if (status) {
                                    String message = response.getString("message");
                                    JSONObject jsonObject = response.getJSONObject("wishlistItem");
//                                currentBook.setItemId(jsonObject.getString("_id"));
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "Failed to add item to wishlist", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                                Log.e("JSON_ERROR", "Error parsing JSON response: " + e.getMessage());
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
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
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

            MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
        }else {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Login")
                    .setMessage("You need to login to add items to wishlist")
                    .setPositiveButton("Proceed to Login", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                            if (context instanceof Activity) {
                                ((Activity) context).finish(); // Finish the current activity
                            }
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            toggleHeartIcon.setImageResource(R.drawable.ic_heart_white);
                            Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        }
    }
    private void removeItemFromWishlist(DashboardModel currentBook) {
//        String removeBookUrl = Constant.BASE_URL + "wishlist/remove/" + currentBook.getBookID();
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, removeBookUrl, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            boolean status = response.getBoolean("status");
//
//                            if (status) {
//                                String message = response.getString("message");
//                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                String errorMessage = "Error: " + error.toString();
//                if (error.networkResponse != null) {
//                    try {
//                        String responseData = new String(error.networkResponse.data, "UTF-8");
//                        errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
//                        errorMessage += "\nResponse Data: " + responseData;
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
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
//        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public int getItemCount() {
        return hardBookECommPurchaseModelArrayList.size();
    }

    public void filter(String query) {
        currentQuery = query;
        hardBookECommPurchaseModelArrayList.clear();
        if (query.isEmpty()) {
            hardBookECommPurchaseModelArrayList.addAll(originalHardBookECommPurchaseModelArrayList);
            heartToggleStates.clear();
            heartToggleStates.addAll(Collections.nCopies(originalHardBookECommPurchaseModelArrayList.size(), false));
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (DashboardModel dashboardModel : originalHardBookECommPurchaseModelArrayList) {
                if (dashboardModel.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        dashboardModel.getContent().toLowerCase().contains(lowerCaseQuery) ||
                        dashboardModel.getTags().toLowerCase().contains(lowerCaseQuery) ||
                        dashboardModel.getPrice().toLowerCase().contains(lowerCaseQuery)) {
                    hardBookECommPurchaseModelArrayList.add(dashboardModel);
                    heartToggleStates.add(false); // Ensure state is added for new entries
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, author, price,category;
        ImageView toggleHeartIcon;
        Button addToCartBtn,goToCartBtn;
        ViewPager2 viewPager;
        LinearLayout dotsLinearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            author = itemView.findViewById(R.id.bookAuthor);
            price = itemView.findViewById(R.id.bookPriceInfo);
            category = itemView.findViewById(R.id.categoryTxtDisplay);
            viewPager = itemView.findViewById(R.id.imgBook);
            toggleHeartIcon = itemView.findViewById(R.id.heartIconToggle);
            addToCartBtn = itemView.findViewById(R.id.addToCartBtn);
            goToCartBtn = itemView.findViewById(R.id.goToCartBtn);
            dotsLinearLayout = itemView.findViewById(R.id.indicatorLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<BookImageModels> bookImageModelsArrayList = new ArrayList<>();
                    ArrayList<String> imageURLArrayList = new ArrayList<>();
                    bookImageModelsArrayList = hardBookECommPurchaseModelArrayList.get(getPosition()).getImages();
                    int length = bookImageModelsArrayList.size();
                    for (int i = 0 ; i < length;i++){
                        String bookImageUrl = bookImageModelsArrayList.get(i).getUrl();
                        imageURLArrayList.add(bookImageUrl);
                    }
                    Intent intent = new Intent(context, ProductDescriptionActivity.class);
                    intent.putStringArrayListExtra("bookImage", imageURLArrayList);
                    intent.putExtra("bookId", hardBookECommPurchaseModelArrayList.get(getAdapterPosition()).getBookID());
                    intent.putExtra("bookTitle", hardBookECommPurchaseModelArrayList.get(getAdapterPosition()).getTitle());
                    intent.putExtra("bookPrice", hardBookECommPurchaseModelArrayList.get(getAdapterPosition()).getPrice());
                    intent.putExtra("bookSellPrice", hardBookECommPurchaseModelArrayList.get(getAdapterPosition()).getSellPrice());
                    intent.putExtra("bookCategory", hardBookECommPurchaseModelArrayList.get(getAdapterPosition()).getCategory());
                    intent.putExtra("bookAuthor", hardBookECommPurchaseModelArrayList.get(getAdapterPosition()).getAuthor());
                    intent.putExtra("bookTags", hardBookECommPurchaseModelArrayList.get(getAdapterPosition()).getTags());
                    intent.putExtra("bookContent", hardBookECommPurchaseModelArrayList.get(getAdapterPosition()).getContent());
                itemView.getContext().startActivity(intent);
                }
            });
        }

        public void setHighlightedText(TextView textView, String text, String query) {
            if (query == null || query.isEmpty()) {
                textView.setText(text);
                return;
            }
            SpannableString spannableString = new SpannableString(text);
            int startIndex = text.toLowerCase().indexOf(query.toLowerCase());
            while (startIndex >= 0) {
                int endIndex = startIndex + query.length();
                spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                startIndex = text.toLowerCase().indexOf(query.toLowerCase(), endIndex);
            }
            textView.setText(spannableString);
        }

        public void setHighlightedPrice(TextView textView, SpannableStringBuilder priceText, String query) {
            if (query == null || query.isEmpty()) {
                textView.setText(priceText);
                return;
            }

            SpannableString spannableString = SpannableString.valueOf(priceText);
            int startIndex = priceText.toString().toLowerCase().indexOf(query.toLowerCase());
            while (startIndex >= 0) {
                int endIndex = startIndex + query.length();
                spannableString.setSpan(new BackgroundColorSpan(Color.YELLOW), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                startIndex = priceText.toString().toLowerCase().indexOf(query.toLowerCase(), endIndex);
            }
            textView.setText(spannableString);
        }
    }
}
