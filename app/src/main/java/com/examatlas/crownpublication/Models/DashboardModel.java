
package com.examatlas.crownpublication.Models;

import com.examatlas.crownpublication.Models.extraModels.BookImageModels;

import java.util.ArrayList;

public class DashboardModel {
    String bookID, title, keyword, price, sellPrice, author, category, content, subject, length, breadth, height, weight, isbn, tags, isInCart, createdAt, updatedAt, isInWishList,edition,publication;
    ArrayList<BookImageModels> images;
    int totalRows, totalPages, currentPage, pageSize;

    public DashboardModel(String bookID, String title, String keyword, String price, String sellPrice, String author, String category, String content, String subject, String length, String breadth, String height, String weight, String isbn, String tags, String isInCart, ArrayList<BookImageModels> images, String createdAt, String updatedAt, String isInWishList, String edition, String publication, int totalRows, int totalPages, int currentPage, int pageSize) {
        this.bookID = bookID;
        this.title = title;
        this.keyword = keyword;
        this.price = price;
        this.sellPrice = sellPrice;
        this.author = author;
        this.category = category;
        this.content = content;
        this.subject = subject;
        this.length = length;
        this.breadth = breadth;
        this.height = height;
        this.weight = weight;
        this.isbn = isbn;
        this.tags = tags;
        this.isInCart = isInCart;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isInWishList = isInWishList;
        this.images = images;
        this.totalRows = totalRows;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.edition = edition;
        this.publication = publication;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
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

    public String getBreadth() {
        return breadth;
    }

    public void setBreadth(String breadth) {
        this.breadth = breadth;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
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

    public String getIsInCart() {
        return isInCart;
    }

    public void setIsInCart(String isInCart) {
        this.isInCart = isInCart;
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

    public String getIsInWishList() {
        return isInWishList;
    }

    public void setIsInWishList(String isInWishList) {
        this.isInWishList = isInWishList;
    }

    public ArrayList<BookImageModels> getImages() {
        return images;
    }

    public void setImages(ArrayList<BookImageModels> images) {
        this.images = images;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
