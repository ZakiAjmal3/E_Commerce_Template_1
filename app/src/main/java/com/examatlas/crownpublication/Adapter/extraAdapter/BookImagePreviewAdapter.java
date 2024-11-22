package com.examatlas.crownpublication.Adapter.extraAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.examatlas.crownpublication.Models.extraModels.BookImageModels;
import com.examatlas.crownpublication.R;

import java.util.ArrayList;
import java.util.List;

public class BookImagePreviewAdapter extends RecyclerView.Adapter<BookImagePreviewAdapter.SliderViewHolder> {
    private final List<String> imageList; // Use List of BookImageModels
    private final LinearLayout indicatorLayout; // The container for the dots
    private final ViewPager2 viewPager;
    public BookImagePreviewAdapter(ArrayList<String> imageList, ViewPager2 viewPager, LinearLayout indicatorLayout){
        this.imageList = imageList;
        this.viewPager = viewPager;
        this.indicatorLayout = indicatorLayout;


        // Set the page change listener for the ViewPager2
        this.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDots(position);
            }
        });

        // Initialize the dots
        setupDots();
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_image_slider_item_layout, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.bind(imageList.get(position)); // Bind the model
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    private void setupDots() {
        indicatorLayout.removeAllViews();
        // Add dots to the indicatorLayout based on the number of images
        for (int i = 0; i < imageList.size(); i++) {
            ImageView dot = new ImageView(viewPager.getContext());
            dot.setImageResource(R.drawable.inactive_dot); // Set the inactive dot by default
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(5, 0, 5, 0); // Adjust margin between dots
            dot.setLayoutParams(params);
            indicatorLayout.addView(dot);
        }
    }

    private void updateDots(int position) {
        // Update the active dot based on the current position
        for (int i = 0; i < indicatorLayout.getChildCount(); i++) {
            ImageView dot = (ImageView) indicatorLayout.getChildAt(i);
            if (i == position) {
                dot.setImageResource(R.drawable.active_dot); // Active dot
            } else {
                dot.setImageResource(R.drawable.inactive_dot); // Inactive dot
            }
        }
    }
    static class SliderViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public SliderViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void bind(String imageModel) {
            Glide.with(imageView.getContext())
//                    .load("https://examatlas-backend.onrender.com/" + imageModel.getFileName())
                    .load(imageModel)
                    .error(R.drawable.book1)
                    .placeholder(R.drawable.book1)
                    .into(imageView);
//            Log.d("BookImageAdapter", "Loading image: " + imageModel.getFileName());
        }
    }
}
