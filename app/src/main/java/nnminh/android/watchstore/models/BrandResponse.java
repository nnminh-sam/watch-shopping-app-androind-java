package nnminh.android.watchstore.models;

import java.util.List;

public class BrandResponse extends BaseResponse {
    private List<Brand> brands;

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }
}
