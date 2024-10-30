package com.examatlas.crownpublication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.crownpublication.Models.DashboardExamCategoryModel;
import com.examatlas.crownpublication.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class DashboardExamCategoryAdapter extends RecyclerView.Adapter<DashboardExamCategoryAdapter.ViewHolder> {
    ArrayList<DashboardExamCategoryModel> dashboardExamCategoryModelArrayList;
    Context context;
    @NonNull
    @Override
    public DashboardExamCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exam_category_recycler_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardExamCategoryAdapter.ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        holder.txtExamName.setText(dashboardExamCategoryModelArrayList.get(position).getExamName());
    }

    @Override
    public int getItemCount() {
        return dashboardExamCategoryModelArrayList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtExamName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtExamName = itemView.findViewById(R.id.examNameTxt);
        }
    }
}
