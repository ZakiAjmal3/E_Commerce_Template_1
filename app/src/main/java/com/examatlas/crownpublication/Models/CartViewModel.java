package com.examatlas.crownpublication.Models;


import com.examatlas.crownpublication.Models.extraModels.BookImageModels;

import java.util.ArrayList;

public class CartViewModel {
    String cartId, bookId, title, keyword, price, sellPrice, author, category, content, subject, length, height, breadth, weight, isbn, tags, createdAt, updatedAt, quantity, itemId, isInCart;
    ArrayList<BookImageModels> bookImageArrayList;

    public CartViewModel(String cartId, String bookId, String title, String keyword, String price, String sellPrice, String author, String category, String content, String subject, String length, String height, String breadth, String weight, String isbn, String tags, ArrayList<BookImageModels> bookImageArrayList, String createdAt, String updatedAt, String quantity, String isInCart, String itemId) {
        this.cartId = cartId;
        this.bookId = bookId;
        this.title = title;
        this.keyword = keyword;
        this.price = price;
        this.sellPrice = sellPrice;
        this.author = author;
        this.category = category;
        this.content = content;
        this.subject = subject;
        this.length = length;
        this.height = height;
        this.breadth = breadth;
        this.weight = weight;
        this.isbn = isbn;
        this.tags = tags;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.quantity = quantity;
        this.itemId = itemId;
        this.isInCart = isInCart;
        this.bookImageArrayList = bookImageArrayList;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
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

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getBreadth() {
        return breadth;
    }

    public void setBreadth(String breadth) {
        this.breadth = breadth;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getIsInCart() {
        return isInCart;
    }

    public void setIsInCart(String isInCart) {
        this.isInCart = isInCart;
    }

    public ArrayList<BookImageModels> getBookImageArrayList() {
        return bookImageArrayList;
    }

    public void setBookImageArrayList(ArrayList<BookImageModels> bookImageArrayList) {
        this.bookImageArrayList = bookImageArrayList;
    }
}