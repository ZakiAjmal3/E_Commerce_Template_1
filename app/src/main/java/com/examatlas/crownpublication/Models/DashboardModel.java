
package com.examatlas.crownpublication.Models;

import com.examatlas.crownpublication.Models.extraModels.BookImageModels;

import java.util.ArrayList;

public class DashboardModel {
    String bookId,bookTitle,sku,slug,publication,stock,price,sellingPrice,description,author,isActive,language,edition,
    categoryId,categoryName,subCategoryId,subCategoryName,tags;
    ArrayList<BookImageModels> bookImages;

    public DashboardModel(String bookId, String bookTitle, String sku, String slug, String publication, String stock, String price, String sellingPrice, String description, String author, String isActive, String language, String edition, String categoryId, String categoryName, String subCategoryId, String subCategoryName, String tags, ArrayList<BookImageModels> bookImages) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.sku = sku;
        this.slug = slug;
        this.publication = publication;
        this.stock = stock;
        this.price = price;
        this.sellingPrice = sellingPrice;
        this.description = description;
        this.author = author;
        this.isActive = isActive;
        this.language = language;
        this.edition = edition;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.subCategoryId = subCategoryId;
        this.subCategoryName = subCategoryName;
        this.tags = tags;
        this.bookImages = bookImages;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
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

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public ArrayList<BookImageModels> getBookImages() {
        return bookImages;
    }

    public void setBookImages(ArrayList<BookImageModels> bookImages) {
        this.bookImages = bookImages;
    }
}
