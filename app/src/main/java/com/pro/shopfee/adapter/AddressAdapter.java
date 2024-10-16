package com.pro.shopfee.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.shopfee.R;
import com.pro.shopfee.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private final List<Address> listAddress;
    private final IClickAddressListener iClickAddressListener;

    public interface IClickAddressListener {
        void onClickAddressItem(Address address);
    }

    public AddressAdapter(List<Address> list, IClickAddressListener listener) {
        this.listAddress = list;
        this.iClickAddressListener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = listAddress.get(position);
        if (address == null) return;
        holder.tvName.setText(address.getName());
        holder.tvPhone.setText(address.getPhone());
        holder.tvAddress.setText(address.getAddress());
        if (address.isSelected()) {
            holder.imgStatus.setImageResource(R.drawable.ic_item_selected);
        } else {
            holder.imgStatus.setImageResource(R.drawable.ic_item_unselect);
        }

        holder.layoutItem.setOnClickListener(view -> iClickAddressListener.onClickAddressItem(address));
    }

    @Override
    public int getItemCount() {
        if (listAddress != null) {
            return listAddress.size();
        }
        return 0;
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgStatus;
        private final TextView tvName;
        private final TextView tvPhone;
        private final TextView tvAddress;
        private final LinearLayout layoutItem;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStatus = itemView.findViewById(R.id.img_status);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvAddress = itemView.findViewById(R.id.tv_address);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}
