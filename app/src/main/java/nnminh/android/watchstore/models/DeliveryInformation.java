package nnminh.android.watchstore.models;

public class DeliveryInformation {
    private String id;
    private String full_name;
    private String city;
    private String district;
    private String street;
    private String specific_address;
    private String phone_number;
    private String is_default;

    public DeliveryInformation() {
    }

    public DeliveryInformation(String id, String full_name, String city, String district, String street, String specific_address, String phone_number, String is_default) {
        this.id = id;
        this.full_name = full_name;
        this.city = city;
        this.district = district;
        this.street = street;
        this.specific_address = specific_address;
        this.phone_number = phone_number;
        this.is_default = is_default;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getSpecific_address() {
        return specific_address;
    }

    public void setSpecific_address(String specific_address) {
        this.specific_address = specific_address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getIs_default() {
        return is_default;
    }

    public void setIs_default(String is_default) {
        this.is_default = is_default;
    }
}
