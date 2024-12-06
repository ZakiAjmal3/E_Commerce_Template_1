package com.examatlas.crownpublication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.crownpublication.Models.TrackingOrderModel;
import com.examatlas.crownpublication.R;
import com.github.vipulasri.timelineview.TimelineView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class TrackingOrderAdapter extends RecyclerView.Adapter<TrackingOrderAdapter.ViewHolder> {
    ArrayList<TrackingOrderModel> trackingOrderModelArrayList;
    Context context;

    public TrackingOrderAdapter(ArrayList<TrackingOrderModel> trackingOrderModelArrayList, Context context) {
        this.trackingOrderModelArrayList = trackingOrderModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public TrackingOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tracking_order_item_timeline, parent, false);
        return new TrackingOrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackingOrderAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(trackingOrderModelArrayList.get(position));

        holder.title.setText(trackingOrderModelArrayList.get(position).getName());

        try {
            // Define the SimpleDateFormat to parse the date
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Ensure it's in UTC

            // Parse the ISO string into a Date object
            Date date = isoFormat.parse(trackingOrderModelArrayList.get(position).getData());

            // Define SimpleDateFormat for date and time with AM/PM format
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a"); // 'a' is for AM/PM

            // Separate the date and time
            String formattedDate = dateFormat.format(date);
            String formattedTime = timeFormat.format(date);
            String dateAndTime = formattedDate + ", " + formattedTime;
            holder.details.setText(dateAndTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (trackingOrderModelArrayList.get(position).getData().equals("---")) {
            if (position == 0){
// Use ContextCompat to get the color resources
                int greyColor = ContextCompat.getColor(context, R.color.dark_grey);
                holder.timeMark.setMarkerColor(greyColor);
                holder.timeMark.setStartLineColor(ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.white));
                holder.timeMark.setEndLineColor(greyColor, greyColor);
            } else if (position == trackingOrderModelArrayList.size() - 1) {
                int greyColor = ContextCompat.getColor(context, R.color.dark_grey);
                holder.timeMark.setMarkerColor(greyColor);
                holder.timeMark.setEndLineColor(ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.white));
                holder.timeMark.setStartLineColor(greyColor, greyColor);

            } else {
                // Use ContextCompat to get the color resources
                int greyColor = ContextCompat.getColor(context, R.color.dark_grey);
                holder.timeMark.setMarkerColor(greyColor);
                holder.timeMark.setStartLineColor(greyColor, greyColor);
                holder.timeMark.setEndLineColor(greyColor, greyColor);
            }
        } else {
            if (position == 0){
// Use ContextCompat to get the color resources
                int greyColor = ContextCompat.getColor(context, R.color.md_theme_dark_errorContainer);
                holder.title.setTextColor(greyColor);
                holder.details.setTextColor(greyColor);
                holder.timeMark.setMarkerColor(greyColor);
                holder.timeMark.setStartLineColor(ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.white));
                holder.timeMark.setEndLineColor(greyColor, greyColor);
            } else if (position == trackingOrderModelArrayList.size() - 1) {
                int greyColor = ContextCompat.getColor(context, R.color.md_theme_dark_errorContainer);
                holder.title.setTextColor(greyColor);
                holder.details.setTextColor(greyColor);
                holder.timeMark.setMarkerColor(greyColor);
                holder.timeMark.setEndLineColor(ContextCompat.getColor(context, R.color.white), ContextCompat.getColor(context, R.color.white));
                holder.timeMark.setStartLineColor(greyColor, greyColor);

            } else {
                // Use ContextCompat to get the color resources
                int errorColor = ContextCompat.getColor(context, R.color.md_theme_dark_errorContainer);
                holder.title.setTextColor(errorColor);
                holder.details.setTextColor(errorColor);
                holder.timeMark.setMarkerColor(errorColor);
                holder.timeMark.setStartLineColor(errorColor, errorColor);
                holder.timeMark.setEndLineColor(errorColor, errorColor);
            }
        }
    }

    @Override
    public int getItemCount() {
        return trackingOrderModelArrayList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,details;
        TimelineView timeMark;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            details = itemView.findViewById(R.id.detail);
            timeMark = itemView.findViewById(R.id.time_marker);
        }
    }
}
