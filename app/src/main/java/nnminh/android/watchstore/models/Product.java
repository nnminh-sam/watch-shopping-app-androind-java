package nnminh.android.watchstore.models;

import java.util.ArrayList;

public class Product {
    private String id;
    private String name;
    private String description;
    private String sku;
    private String spu;
    private double price;
    private int stock;
    private int sold;
    private String status;
    private ArrayList<String> assets;
    private ArrayList<Spec> specs;
    private Brand brand;
    private ArrayList<Category> categories;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSpu() {
        return spu;
    }

    public void setSpu(String spu) {
        this.spu = spu;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getAssets() {
        return assets;
    }

    public void setAssets(ArrayList<String> assets) {
        this.assets = assets;
    }

    public ArrayList<Spec> getSpecs() {
        return specs;
    }

    public void setSpecs(ArrayList<Spec> specs) {
        this.specs = specs;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }
}