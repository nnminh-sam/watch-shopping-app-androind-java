package nnminh.android.watchstore.models;

import java.util.List;

public class ProductResponse extends BaseResponse {
    private List<Product> products;
    private Pagination pagination;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
