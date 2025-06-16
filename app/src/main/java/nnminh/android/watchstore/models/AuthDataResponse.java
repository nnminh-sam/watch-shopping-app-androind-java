package nnminh.android.watchstore.models;

public class AuthDataResponse {
    private String access_token;
    private String refresh_token;
    private User user;

    public String getAccess_token() {
        return access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public User getUser() {
        return user;
    }
}
