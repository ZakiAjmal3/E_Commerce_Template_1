package com.examatlas.crownpublication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.crownpublication.Adapter.extraAdapter.BookImageAdapter;
import com.examatlas.crownpublication.DashboardActivity;
import com.examatlas.crownpublication.Models.DashboardCategoryModel;
import com.examatlas.crownpublication.Models.DashboardModel;
import com.examatlas.crownpublication.R;

import java.util.ArrayList;
import java.util.List;

public class DashboardCategoryAdapter extends RecyclerView.Adapter<DashboardCategoryAdapter.ViewHolder> {
    ArrayList<DashboardCategoryModel> dashboardCategoryModelArrayList;
    Context context;

    public DashboardCategoryAdapter(ArrayList<DashboardCategoryModel> dashboardCategoryModelArrayList, Context context) {
        this.dashboardCategoryModelArrayList = dashboardCategoryModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public DashboardCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_recycler_item_layout, parent, false);
        return new DashboardCategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardCategoryAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(dashboardCategoryModelArrayList.get(position));

        holder.textView.setText(dashboardCategoryModelArrayList.get(position).getCategoryName());
    }

    @Override
    public int getItemCount() {
        return dashboardCategoryModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.examNameTxt);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((DashboardActivity) context).sortBookWithExamName(dashboardCategoryModelArrayList.get(getPosition()).getCategoryName());
                }
            });
        }
    }
}
