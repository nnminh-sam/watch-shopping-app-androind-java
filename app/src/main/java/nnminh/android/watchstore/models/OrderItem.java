package nnminh.android.watchstore.models;

import java.util.List;

public class OrderItem {
    private String id;
    private String product_id;
    private String name;
    private List<Spec> specs;
    private int quantity;
    private Long price;
    private String asset;

    public OrderItem() {
    }

    public OrderItem(String id, String product_id, String name, List<Spec> specs, int quantity, Long price, String asset) {
        this.id = id;
        this.product_id = product_id;
        this.name = name;
        this.specs = specs;
        this.quantity = quantity;
        this.price = price;
        this.asset = asset;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Spec> getSpecs() {
        return specs;
    }

    public void setSpecs(List<Spec> specs) {
        this.specs = specs;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }
}
