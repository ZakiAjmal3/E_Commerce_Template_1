package com.examatlas.crownpublication.Models.extraModels;

public class OrderItemsArrayListModel {
    String bookId,title,sellPrice,quantity;

    public OrderItemsArrayListModel(String bookId, String title, String sellPrice, String quantity) {
        this.bookId = bookId;
        this.title = title;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
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

}
