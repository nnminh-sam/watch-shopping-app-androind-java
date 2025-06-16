package nnminh.android.watchstore.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class CartItem implements Parcelable {
    private String id;
    private String product_id;
    private String name;
    private Long price;
    private int quantity;
    private List<Spec> specs;
    private String asset;

    public CartItem() {
    }

    protected CartItem(Parcel in) {
        id = in.readString();
        product_id = in.readString();
        name = in.readString();
        price = in.readLong();
        quantity = in.readInt();
        specs = new ArrayList<>();
        in.readList(specs, Spec.class.getClassLoader());
        asset = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(product_id);
        dest.writeString(name);
        dest.writeLong(price != null ? price : 0L);
        dest.writeInt(quantity);
        dest.writeList(specs);
        dest.writeString(asset);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<Spec> getSpecs() {
        return specs;
    }

    public void setSpecs(List<Spec> specs) {
        this.specs = specs;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }
}
