package com.pro.shopfee.adapter.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.shopfee.R;
import com.pro.shopfee.listener.IOnAdminManagerCategoryListener;
import com.pro.shopfee.model.Category;

import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.AdminCategoryViewHolder> {

    private final List<Category> mListCategory;
    private final IOnAdminManagerCategoryListener mListener;

    public AdminCategoryAdapter(List<Category> list, IOnAdminManagerCategoryListener listener) {
        this.mListCategory = list;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AdminCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_category, parent, false);
        return new AdminCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminCategoryViewHolder holder, int position) {
        Category category = mListCategory.get(position);
        if (category == null) return;
        holder.tvName.setText(category.getName());
        holder.imgEdit.setOnClickListener(v -> mListener.onClickUpdateCategory(category));
        holder.imgDelete.setOnClickListener(v -> mListener.onClickDeleteCategory(category));
        holder.layoutItem.setOnClickListener(v -> mListener.onClickItemCategory(category));
    }

    @Override
    public int getItemCount() {
        if (mListCategory != null) {
            return mListCategory.size();
        }
        return 0;
    }

    public static class AdminCategoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final ImageView imgEdit;
        private final ImageView imgDelete;
        private final RelativeLayout layoutItem;

        public AdminCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}
