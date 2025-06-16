package nnminh.android.watchstore.models;

import java.util.Date;
import java.util.List;

public class Order {
    private String id;
    private String user_id;
    private String order_number;
    private DeliveryInformation delivery_information;
    private List<OrderItem> details;
    private Long total;
    private String status;
    private Date created_at;

    public Order() {
    }

    public Order(String id, String user_id, String order_number, DeliveryInformation delivery_information, List<OrderItem> details, Long total, String status, Date created_at) {
        this.id = id;
        this.user_id = user_id;
        this.order_number = order_number;
        this.delivery_information = delivery_information;
        this.details = details;
        this.total = total;
        this.status = status;
        this.created_at = created_at;
    }

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

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }

    public DeliveryInformation getDelivery_information() {
        return delivery_information;
    }

    public void setDelivery_information(DeliveryInformation delivery_information) {
        this.delivery_information = delivery_information;
    }

    public List<OrderItem> getDetails() {
        return details;
    }

    public void setDetails(List<OrderItem> details) {
        this.details = details;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}