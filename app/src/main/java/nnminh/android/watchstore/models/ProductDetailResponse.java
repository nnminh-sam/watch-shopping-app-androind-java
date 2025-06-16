package nnminh.android.watchstore.models;

public class ProductDetailResponse extends BaseResponse {
    private Product product;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}