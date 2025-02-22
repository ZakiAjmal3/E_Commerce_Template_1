package com.examatlas.crownpublication.Models;

import com.examatlas.crownpublication.Models.extraModels.OrderItemsArrayListModel;

import java.util.ArrayList;

public class OrderHistoryModel {
    String order_id,shipRocketOrderId,totalAmount,finalAmount,paymentMethod,status,addressType,shippingAddressUserName,shippingAddressFull,razorpay_order_id,createdAt;
    ArrayList<OrderItemsArrayListModel> orderItemsArrayList;

    public OrderHistoryModel(String order_id, String shipRocketOrderId, String totalAmount, String finalAmount, String paymentMethod, String status, String addressType, String shippingAddressUserName, String shippingAddressFull, String razorpay_order_id, String createdAt, ArrayList<OrderItemsArrayListModel> orderItemsArrayList) {
        this.order_id = order_id;
        this.shipRocketOrderId = shipRocketOrderId;
        this.totalAmount = totalAmount;
        this.finalAmount = finalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.addressType = addressType;
        this.shippingAddressUserName = shippingAddressUserName;
        this.shippingAddressFull = shippingAddressFull;
        this.razorpay_order_id = razorpay_order_id;
        this.createdAt = createdAt;
        this.orderItemsArrayList = orderItemsArrayList;
    }

    public String getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(String finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getShipRocketOrderId() {
        return shipRocketOrderId;
    }

    public void setShipRocketOrderId(String shipRocketOrderId) {
        this.shipRocketOrderId = shipRocketOrderId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getShippingAddressUserName() {
        return shippingAddressUserName;
    }

    public void setShippingAddressUserName(String shippingAddressUserName) {
        this.shippingAddressUserName = shippingAddressUserName;
    }

    public String getShippingAddressFull() {
        return shippingAddressFull;
    }

    public void setShippingAddressFull(String shippingAddressFull) {
        this.shippingAddressFull = shippingAddressFull;
    }

    public String getRazorpay_order_id() {
        return razorpay_order_id;
    }

    public void setRazorpay_order_id(String razorpay_order_id) {
        this.razorpay_order_id = razorpay_order_id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public ArrayList<OrderItemsArrayListModel> getOrderItemsArrayList() {
        return orderItemsArrayList;
    }

    public void setOrderItemsArrayList(ArrayList<OrderItemsArrayListModel> orderItemsArrayList) {
        this.orderItemsArrayList = orderItemsArrayList;
    }
}
