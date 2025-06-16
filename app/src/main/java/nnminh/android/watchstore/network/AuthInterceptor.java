package nnminh.android.watchstore.network;

import java.io.IOException;

import nnminh.android.watchstore.auth.TokenManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import android.content.Context;

public class AuthInterceptor implements Interceptor {
    private final TokenManager tokenManager;

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder();

        String token = tokenManager.getToken();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }
        Request request = builder.build();
        return chain.proceed(request);
    }
}