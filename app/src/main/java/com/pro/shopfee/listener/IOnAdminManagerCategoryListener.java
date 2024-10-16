package com.pro.shopfee.listener;

import com.pro.shopfee.model.Category;

public interface IOnAdminManagerCategoryListener {
    void onClickUpdateCategory(Category category);
    void onClickDeleteCategory(Category category);
    void onClickItemCategory(Category category);
}
