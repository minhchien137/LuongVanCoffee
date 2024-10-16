package com.pro.shopfee.adapter.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.shopfee.R;
import com.pro.shopfee.listener.IOnAdminManagerToppingListener;
import com.pro.shopfee.model.Topping;
import com.pro.shopfee.utils.Constant;

import java.util.List;

public class AdminToppingAdapter extends RecyclerView.Adapter<AdminToppingAdapter.AdminToppingViewHolder> {

    private final List<Topping> mListTopping;
    private final IOnAdminManagerToppingListener mListener;

    public AdminToppingAdapter(List<Topping> list, IOnAdminManagerToppingListener listener) {
        this.mListTopping = list;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AdminToppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_topping, parent, false);
        return new AdminToppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminToppingViewHolder holder, int position) {
        Topping topping = mListTopping.get(position);
        if (topping == null) return;
        holder.tvName.setText(topping.getName());
        String strPrice = topping.getPrice() + Constant.CURRENCY;
        holder.tvPrice.setText(strPrice);
        holder.imgEdit.setOnClickListener(v -> mListener.onClickUpdateTopping(topping));
        holder.imgDelete.setOnClickListener(v -> mListener.onClickDeleteTopping(topping));
    }

    @Override
    public int getItemCount() {
        if (mListTopping != null) {
            return mListTopping.size();
        }
        return 0;
    }

    public static class AdminToppingViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvPrice;
        private final ImageView imgEdit;
        private final ImageView imgDelete;

        public AdminToppingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}
