package nnminh.android.watchstore.models;

public class UpdateProfileRequest {
    private String first_name;
    private String last_name;
    private String gender;
    private String phone_number;
    private String date_of_birth;

    public UpdateProfileRequest(String first_name, String last_name, String gender, String phone_number, String date_of_birth) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.gender = gender;
        this.phone_number = phone_number;
        this.date_of_birth = date_of_birth;
    }
}