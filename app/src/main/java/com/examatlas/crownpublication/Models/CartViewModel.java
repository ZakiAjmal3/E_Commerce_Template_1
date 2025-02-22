package com.examatlas.crownpublication.Models;


import com.examatlas.crownpublication.Models.extraModels.BookImageModels;

import java.util.ArrayList;

public class CartViewModel {
    String cartId, quantity,type,bookId, title,sku,slug,publication,stock, price, sellingPrice, description ,author,categoryId,subCategoryId,isActive,language,edition,tags;
    ArrayList<BookImageModels> bookImageArrayList;

    public CartViewModel(String cartId, String quantity, String type, String bookId, String title, String sku, String slug, String publication, String stock, String price, String sellingPrice, String description, String author, String categoryId, String subCategoryId, String isActive, String language, String edition, String tags, ArrayList<BookImageModels> bookImageArrayList) {
        this.cartId = cartId;
        this.quantity = quantity;
        this.type = type;
        this.bookId = bookId;
        this.title = title;
        this.sku = sku;
        this.slug = slug;
        this.publication = publication;
        this.stock = stock;
        this.price = price;
        this.sellingPrice = sellingPrice;
        this.description = description;
        this.author = author;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.isActive = isActive;
        this.language = language;
        this.edition = edition;
        this.tags = tags;
        this.bookImageArrayList = bookImageArrayList;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(String subCategoryId) {
        this.subCategoryId = subCategoryId;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public ArrayList<BookImageModels> getBookImageArrayList() {
        return bookImageArrayList;
    }

    public void setBookImageArrayList(ArrayList<BookImageModels> bookImageArrayList) {
        this.bookImageArrayList = bookImageArrayList;
    }
}