package nnminh.android.watchstore.models;

import java.util.List;

public class CategoryResponse extends BaseResponse {
    private List<Category> categories;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
