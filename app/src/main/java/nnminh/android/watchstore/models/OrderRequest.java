package nnminh.android.watchstore.models;

import java.util.List;

public class OrderRequest {
    private String full_name;
    private String phone_number;
    private String city;
    private String district;
    private String street;
    private String specific_address;
    private List<String> cart_detail_ids;
    private String payment_method;
}