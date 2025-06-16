package nnminh.android.watchstore.models;

import java.util.HashMap;
import java.util.Map;

public class FilterRequest {
    private String name;
    private String brandName;
    private Long min_price;
    private Long max_price;
    private String categoryName;
    private int page;
    private int size;

    public FilterRequest() {
    }

    public FilterRequest(String name, String brandName, Long min_price, Long max_price, String categoryName) {
        this.name = name;
        this.brandName = brandName;
        this.min_price = min_price;
        this.max_price = max_price;
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Long getMin_price() {
        return min_price;
    }

    public void setMin_price(Long min_price) {
        this.min_price = min_price;
    }

    public Long getMax_price() {
        return max_price;
    }

    public void setMax_price(Long max_price) {
        this.max_price = max_price;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public static Map<String, Object> filterRequestToMap(FilterRequest filter) {
        Map<String, Object> map = new HashMap<>();
        if (filter.getName() != null) map.put("name", filter.getName());
        if (filter.getBrandName() != null) map.put("brand", filter.getBrandName());
        if (filter.getMin_price() != null) map.put("min_price", filter.getMin_price());
        if (filter.getMax_price() != null) map.put("max_price", filter.getMax_price());
        if (filter.getCategoryName() != null) map.put("category", filter.getCategoryName());
        if (filter.getPage() != 0) map.put("page", filter.getPage());
        if (filter.getSize() != 0) map.put("size", filter.getSize());
        return map;
    }

    @Override
    public String toString() {
        return "FilterRequest{" +
                "name='" + name + '\'' +
                ", brandName='" + brandName + '\'' +
                ", min_price=" + min_price +
                ", max_price=" + max_price +
                ", categoryName='" + categoryName + '\'' +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}