package nnminh.android.watchstore.models;

import java.util.List;

public class CreateOrderRequest {
    private List<String> cart_detail_ids;
    private String payment_method;
    private String deliveryAddressId;
    private DeliveryInformation deliveryAddress;

    public CreateOrderRequest(List<String> cart_detail_ids, String payment_method, String deliveryAddressId, DeliveryInformation deliveryAddress) {
        this.cart_detail_ids = cart_detail_ids;
        this.payment_method = payment_method;
        this.deliveryAddressId = deliveryAddressId;
        this.deliveryAddress = deliveryAddress;
    }

    public List<String> getCart_detail_ids() {
        return cart_detail_ids;
    }

    public void setCart_detail_ids(List<String> cart_detail_ids) {
        this.cart_detail_ids = cart_detail_ids;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getDeliveryAddressId() {
        return deliveryAddressId;
    }

    public void setDeliveryAddressId(String deliveryAddressId) {
        this.deliveryAddressId = deliveryAddressId;
    }

    public DeliveryInformation getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(DeliveryInformation deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
