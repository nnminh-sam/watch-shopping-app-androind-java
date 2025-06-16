package nnminh.android.watchstore.models;

public class UserResponse extends BaseResponse {
    private UserProfile user;

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }
}
