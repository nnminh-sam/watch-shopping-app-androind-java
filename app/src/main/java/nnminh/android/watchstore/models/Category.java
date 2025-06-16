package nnminh.android.watchstore.models;

import java.util.ArrayList;

public class Category {
    private String id;
    private String name;
    private String slug;
    private String description;
    private ArrayList<String> assets;
    private Boolean is_feature;

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

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getAssets() {
        return assets;
    }

    public void setAssets(ArrayList<String> assets) {
        this.assets = assets;
    }

    public Boolean getIs_feature() {
        return is_feature;
    }

    public void setIs_feature(Boolean is_feature) {
        this.is_feature = is_feature;
    }
}
