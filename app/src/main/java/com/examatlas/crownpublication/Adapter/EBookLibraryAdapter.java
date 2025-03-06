package com.examatlas.crownpublication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.examatlas.crownpublication.Models.DashboardModel;
import com.examatlas.crownpublication.Models.extraModels.BookImageModels;
import com.examatlas.crownpublication.PurchasedEBookViewingBookActivity;
import com.examatlas.crownpublication.R;
import com.examatlas.crownpublication.Utils.SessionManager;

import java.util.ArrayList;
import java.util.Collections;

public class EBookLibraryAdapter extends RecyclerView.Adapter<EBookLibraryAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<DashboardModel> allBooksModelArrayList;
    private final ArrayList<DashboardModel> originalAllBooksModelArrayList;
    private String currentQuery = "";
    SessionManager sessionManager;
    public EBookLibraryAdapter(Context context, ArrayList<DashboardModel> allBooksModelArrayList) {
        this.originalAllBooksModelArrayList = new ArrayList<>(allBooksModelArrayList);
        this.allBooksModelArrayList = new ArrayList<>(originalAllBooksModelArrayList);
        this.context = context;
// Check if context is valid before initializing SessionManager
        if (context != null) {
            sessionManager = new SessionManager(context.getApplicationContext());
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ebook_library_item_list2, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DashboardModel currentBook = allBooksModelArrayList.get(position);
        holder.itemView.setTag(currentBook);

        holder.price.setVisibility(View.GONE);
        holder.deliveryTxt.setVisibility(View.GONE);

        holder.title.setText(currentBook.getBookTitle());
        // Set the title to one line and add ellipsis if it exceeds
        holder.title.setEllipsize(TextUtils.TruncateAt.END);
        holder.title.setMaxLines(1);

        // Set the book image
        ArrayList<BookImageModels> bookImageModelsArrayList = currentBook.getBookImages();
        if (!bookImageModelsArrayList.isEmpty()) {
            String imageUrl = currentBook.getBookImages().get(0).getUrl();
            Glide.with(context)
                    .load(imageUrl)
                    .error(R.drawable.noimage)
                    .placeholder(R.drawable.noimage)
                    .into(holder.bookImg);
        } else {
            Glide.with(context)
                    .load(R.drawable.noimage)
                    .into(holder.bookImg);
        }
    }


    @Override
    public int getItemCount() {
        return allBooksModelArrayList.size();
    }
    public void filter(String query) {
        currentQuery = query;
        allBooksModelArrayList.clear();
        if (query.isEmpty()) {
            allBooksModelArrayList.addAll(originalAllBooksModelArrayList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (DashboardModel dashboardModel : originalAllBooksModelArrayList) {
                if (dashboardModel.getBookTitle().toLowerCase().contains(lowerCaseQuery) ||
                        dashboardModel.getCategoryName().toLowerCase().contains(lowerCaseQuery) ||
                        dashboardModel.getTags().toLowerCase().contains(lowerCaseQuery) ||
                        dashboardModel.getPrice().toLowerCase().contains(lowerCaseQuery) ||
                        dashboardModel.getAuthor().toLowerCase().contains(lowerCaseQuery) ||
                        dashboardModel.getSellingPrice().toLowerCase().contains(lowerCaseQuery)) {
                    allBooksModelArrayList.add(dashboardModel);
                }
            }
        }
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price,deliveryTxt;
        ImageView bookImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            price = itemView.findViewById(R.id.bookPriceInfo);
            deliveryTxt = itemView.findViewById(R.id.deliveryTypeTxt);
            bookImg = itemView.findViewById(R.id.imgBook);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = allBooksModelArrayList.get(getAdapterPosition()).getBookTitle();
                    Intent intent = new Intent(context, PurchasedEBookViewingBookActivity.class);
                    intent.putExtra("bookId", allBooksModelArrayList.get(getAdapterPosition()).getBookId());
                    intent.putExtra("title", title);
                    Log.e("Title", title);
                    context.startActivity(intent);
                }
            });
        }
    }
}
