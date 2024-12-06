package com.examatlas.crownpublication.Models.extraModels;

public class OrderItemsArrayListModel {
    String bookId,itemId,title,sellPrice,quantity,isInCart;

    public OrderItemsArrayListModel(String bookId, String itemId, String title, String sellPrice, String quantity, String isInCart) {
        this.bookId = bookId;
        this.itemId = itemId;
        this.title = title;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
        this.isInCart = isInCart;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(String sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getIsInCart() {
        return isInCart;
    }

    public void setIsInCart(String isInCart) {
        this.isInCart = isInCart;
    }
}
