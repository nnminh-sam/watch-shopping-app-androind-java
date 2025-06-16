package nnminh.android.watchstore.utils;

import android.app.Activity;
import android.content.Context;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.badge.BadgeDrawable;
import nnminh.android.watchstore.R;
import nnminh.android.watchstore.auth.TokenManager;
import nnminh.android.watchstore.models.CartResponse;
import nnminh.android.watchstore.network.ApiClient;
import nnminh.android.watchstore.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartBadgeHelper {

    public static void updateCartBadge(Activity activity) {
        String token = TokenManager.getInstance(activity).getToken();
        ApiService apiService = ApiClient.getClient(activity).create(ApiService.class);
        apiService.getCart(token).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCart().getDetails() != null) {
                    int count = 0;
                    for (var item : response.body().getCart().getDetails()) {
                        count += item.getQuantity();
                    }
                    setBadge(activity, count);
                } else {
                    setBadge(activity, 0);
                }
            }
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                setBadge(activity, 0);
            }
        });
    }

    private static void setBadge(Activity activity, int count) {
        activity.runOnUiThread(() -> {
            BottomNavigationView nav = activity.findViewById(R.id.bottom_navigation);
            if (nav != null) {
                BadgeDrawable badge = nav.getOrCreateBadge(R.id.nav_cart);
                if (count > 0) {
                    badge.setVisible(true);
                    badge.setNumber(count);
                } else {
                    badge.setVisible(false);
                }
            }
        });
    }
}