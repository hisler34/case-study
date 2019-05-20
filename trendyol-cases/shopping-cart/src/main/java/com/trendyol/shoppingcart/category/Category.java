package com.trendyol.shoppingcart.category;

import com.trendyol.shoppingcart.utilities.Utils;
import lombok.ToString;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class Category implements ICategory {

    private String title;
    private ICategory parentCategory;

    public Category(String title) {
        this.title = title;
    }

    public Category(String title, ICategory parentCategory) {
        this.title = title;
        this.parentCategory = parentCategory;

    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public ICategory getParentCategory() {
        return this.parentCategory;
    }


    @Override
    public Collection<? extends ICategory> getAllCategory() {
        Set<ICategory> categories = new HashSet<>();
        categories.add(getParentCategory());
        categories.add(this);
        return categories;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setParentCategory(ICategory parentCategory) {
        this.parentCategory = parentCategory;
    }


    @Override
    public String toString() {
     return Utils.toZtring(this,"Category ");
    }
}