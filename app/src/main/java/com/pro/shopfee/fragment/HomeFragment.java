package com.pro.shopfee.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.pro.shopfee.MyApplication;
import com.pro.shopfee.R;
import com.pro.shopfee.activity.DrinkDetailActivity;
import com.pro.shopfee.adapter.BannerViewPagerAdapter;
import com.pro.shopfee.adapter.CategoryPagerAdapter;
import com.pro.shopfee.event.SearchKeywordEvent;
import com.pro.shopfee.model.Category;
import com.pro.shopfee.model.Drink;
import com.pro.shopfee.utils.Constant;
import com.pro.shopfee.utils.GlobalFunction;
import com.pro.shopfee.utils.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class HomeFragment extends Fragment {

    private View mView;
    private ViewPager2 viewPagerDrinkFeatured;
    private CircleIndicator3 indicatorDrinkFeatured;
    private ViewPager2 viewPagerCategory;
    private TabLayout tabCategory;
    private EditText edtSearchName;
    private ImageView imgSearch;

    private List<Drink> listDrinkFeatured;
    private List<Category> listCategory;
    private ValueEventListener mCategoryValueEventListener;
    private ValueEventListener mDrinkValueEventListener;

    private final Handler mHandlerBanner = new Handler();
    private final Runnable mRunnableBanner = new Runnable() {
        @Override
        public void run() {
            if (viewPagerDrinkFeatured == null || listDrinkFeatured == null || listDrinkFeatured.isEmpty()) {
                return;
            }
            if (viewPagerDrinkFeatured.getCurrentItem() == listDrinkFeatured.size() - 1) {
                viewPagerDrinkFeatured.setCurrentItem(0);
                return;
            }
            viewPagerDrinkFeatured.setCurrentItem(viewPagerDrinkFeatured.getCurrentItem() + 1);
        }
    };

}
