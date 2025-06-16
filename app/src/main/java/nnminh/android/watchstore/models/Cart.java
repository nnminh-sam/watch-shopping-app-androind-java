package nnminh.android.watchstore.models;

import java.util.List;

public class Cart {
    private String id;
    private String user_id;
    private Long total;
    private List<CartItem> details;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<CartItem> getDetails() {
        return details;
    }

    public void setDetails(List<CartItem> details) {
        this.details = details;
    }
}