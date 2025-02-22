package com.examatlas.crownpublication.Models;

public class DashboardCategoryModel {
    String categoryId,categoryName,slug,isActive;

    public DashboardCategoryModel(String categoryId, String categoryName, String slug, String isActive) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.slug = slug;
        this.isActive = isActive;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
