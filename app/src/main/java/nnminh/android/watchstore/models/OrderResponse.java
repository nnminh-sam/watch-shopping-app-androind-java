package nnminh.android.watchstore.models;

import java.util.List;

public class OrderResponse extends BaseResponse {
    private List<Order> orders;

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}