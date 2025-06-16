package nnminh.android.watchstore.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Spec implements Parcelable {
    private String id;
    private String key;
    private String value;
    private String type;
    private String url;

    public Spec() {
    }

    protected Spec(Parcel in) {
        id = in.readString();
        key = in.readString();
        value = in.readString();
        type = in.readString();
        url = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(value);
        dest.writeString(type);
        dest.writeString(url);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Spec> CREATOR = new Creator<Spec>() {
        @Override
        public Spec createFromParcel(Parcel in) {
            return new Spec(in);
        }

        @Override
        public Spec[] newArray(int size) {
            return new Spec[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
