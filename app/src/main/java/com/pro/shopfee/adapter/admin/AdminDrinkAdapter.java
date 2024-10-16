package com.pro.shopfee.adapter.admin;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.shopfee.R;
import com.pro.shopfee.listener.IOnAdminManagerDrinkListener;
import com.pro.shopfee.model.Drink;
import com.pro.shopfee.utils.Constant;
import com.pro.shopfee.utils.GlideUtils;

import java.util.List;

public class AdminDrinkAdapter extends RecyclerView.Adapter<AdminDrinkAdapter.AdminDrinkViewHolder> {

    private final List<Drink> listDrink;
    private final IOnAdminManagerDrinkListener mListener;

    public AdminDrinkAdapter(List<Drink> list, IOnAdminManagerDrinkListener listener) {
        this.listDrink = list;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AdminDrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_drink, parent, false);
        return new AdminDrinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminDrinkViewHolder holder, int position) {
        Drink drink = listDrink.get(position);
        if (drink == null) return;

        GlideUtils.loadUrl(drink.getImage(), holder.imgProduct);
        holder.tvName.setText(drink.getName());

        if (drink.getSale() <= 0) {
            holder.tvPrice.setVisibility(View.GONE);
            String strPrice = drink.getPrice() + Constant.CURRENCY;
            holder.tvPriceSale.setText(strPrice);
        } else {
            holder.tvPrice.setVisibility(View.VISIBLE);

            String strOldPrice = drink.getPrice() + Constant.CURRENCY;
            holder.tvPrice.setText(strOldPrice);
            holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            String strRealPrice = drink.getRealPrice() + Constant.CURRENCY;
            holder.tvPriceSale.setText(strRealPrice);
        }
        if (drink.getCategory_id() > 0) {
            holder.layoutCategory.setVisibility(View.VISIBLE);
            holder.tvCategory.setText(drink.getCategory_name());
        } else {
            holder.layoutCategory.setVisibility(View.GONE);
        }
        if (drink.isFeatured()) {
            holder.tvFeatured.setText("Có");
        } else {
            holder.tvFeatured.setText("Không");
        }

        holder.imgEdit.setOnClickListener(view -> mListener.onClickUpdateDrink(drink));
        holder.imgDelete.setOnClickListener(view -> mListener.onClickDeleteDrink(drink));
    }

    @Override
    public int getItemCount() {
        if (listDrink != null) {
            return listDrink.size();
        }
        return 0;
    }

    public static class AdminDrinkViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgProduct;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvPriceSale;
        private final LinearLayout layoutCategory;
        private final TextView tvCategory;
        private final TextView tvFeatured;
        private final ImageView imgEdit;
        private final ImageView imgDelete;

        public AdminDrinkViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvPriceSale = itemView.findViewById(R.id.tv_price_sale);
            layoutCategory = itemView.findViewById(R.id.layout_category);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvFeatured = itemView.findViewById(R.id.tv_featured);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}
