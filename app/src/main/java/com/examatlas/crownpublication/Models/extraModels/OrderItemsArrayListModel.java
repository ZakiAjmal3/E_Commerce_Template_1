package com.examatlas.crownpublication.Models.extraModels;

public class OrderItemsArrayListModel {
    String bookId,title,bookType,sellPrice,quantity;

    public OrderItemsArrayListModel(String bookId, String title, String bookType, String sellPrice, String quantity) {
        this.bookId = bookId;
        this.title = title;
        this.bookType = bookType;
        this.sellPrice = sellPrice;
        this.quantity = quantity;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
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
