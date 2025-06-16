package nnminh.android.watchstore.models;

public class SingleOrderResponse extends BaseResponse {
    private Order order;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
