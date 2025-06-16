package nnminh.android.watchstore.models;

public class CartResponse extends BaseResponse{
    private Cart cart;

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }
}