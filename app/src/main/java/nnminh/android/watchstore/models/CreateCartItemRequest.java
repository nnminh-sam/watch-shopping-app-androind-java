package nnminh.android.watchstore.models;

public class CreateCartItemRequest {
    private String product_id;
    private int quantity;

    public CreateCartItemRequest(String product_id, int quantity) {
        this.product_id = product_id;
        this.quantity = quantity;
    }
}