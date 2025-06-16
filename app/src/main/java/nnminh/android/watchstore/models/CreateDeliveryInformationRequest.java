package nnminh.android.watchstore.models;

public class CreateDeliveryInformationRequest {
    private String full_name;
    private String phone_number;
    private String city;
    private String district;
    private String street;
    private String specific_address;
    private boolean is_default;

    public CreateDeliveryInformationRequest(String full_name, String phone_number, String city, String district, String street, String specific_address, boolean is_default) {
        this.full_name = full_name;
        this.phone_number = phone_number;
        this.city = city;
        this.district = district;
        this.street = street;
        this.specific_address = specific_address;
        this.is_default = is_default;
    }
}
