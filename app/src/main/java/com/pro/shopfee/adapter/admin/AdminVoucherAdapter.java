package com.pro.shopfee.adapter.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.shopfee.R;
import com.pro.shopfee.listener.IOnAdminManagerVoucherListener;
import com.pro.shopfee.model.Voucher;

import java.util.List;

public class AdminVoucherAdapter extends RecyclerView.Adapter<AdminVoucherAdapter.AdminVoucherViewHolder> {

    private final List<Voucher> mListVoucher;
    private final IOnAdminManagerVoucherListener mListener;

    public AdminVoucherAdapter(List<Voucher> list, IOnAdminManagerVoucherListener listener) {
        this.mListVoucher = list;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AdminVoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_voucher, parent, false);
        return new AdminVoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminVoucherViewHolder holder, int position) {
        Voucher voucher = mListVoucher.get(position);
        if (voucher == null) return;
        holder.tvTitle.setText(voucher.getTitle());
        holder.tvMinimum.setText(voucher.getMinimumText());
        holder.imgEdit.setOnClickListener(v -> mListener.onClickUpdateVoucher(voucher));
        holder.imgDelete.setOnClickListener(v -> mListener.onClickDeleteVoucher(voucher));
    }

    @Override
    public int getItemCount() {
        if (mListVoucher != null) {
            return mListVoucher.size();
        }
        return 0;
    }

    public static class AdminVoucherViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTitle;
        private final TextView tvMinimum;
        private final ImageView imgEdit;
        private final ImageView imgDelete;

        public AdminVoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvMinimum = itemView.findViewById(R.id.tv_minimum);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}
