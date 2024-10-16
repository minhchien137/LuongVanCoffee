package com.pro.shopfee.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.shopfee.R;
import com.pro.shopfee.listener.IClickDrinkListener;
import com.pro.shopfee.model.Drink;
import com.pro.shopfee.utils.GlideUtils;

import java.util.List;

public class BannerViewPagerAdapter extends RecyclerView.Adapter<BannerViewPagerAdapter.BannerViewHolder> {

    private final List<Drink> mListDrink;
    private final IClickDrinkListener iClickDrinkListener;

    public BannerViewPagerAdapter(List<Drink> list, IClickDrinkListener listener) {
        this.mListDrink = list;
        this.iClickDrinkListener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Drink drink = mListDrink.get(position);
        if (drink == null) return;
        GlideUtils.loadUrlBanner(drink.getBanner(), holder.imgBanner);
        holder.imgBanner.setOnClickListener(view
                -> iClickDrinkListener.onClickDrinkItem(drink));
    }

    @Override
    public int getItemCount() {
        if (mListDrink != null) {
            return mListDrink.size();
        }
        return 0;
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgBanner;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.img_banner);
        }
    }
}
