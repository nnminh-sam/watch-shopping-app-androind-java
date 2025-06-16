package nnminh.android.watchstore.network;

import java.util.Map;

import nnminh.android.watchstore.models.*;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    // ──────── Auth ────────

    @POST("auth/sign-up")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("auth/sign-in")
    Call<AuthResponse> login(@Body LoginRequest request);

    @GET("users/my")
    Call<UserResponse> getUserProfile(@Header("Authorization") String token);

    @PATCH("users")
    Call<UserResponse> updateProfile(@Header("Authorization") String token, @Body UpdateProfileRequest request);

    @Multipart
    @PATCH("users/avatar")
    Call<BaseResponse> updateAvatar(
            @Header("Authorization") String token,
            @Part MultipartBody.Part file
    );

    @POST("users/delivery-addresses")
    Call<DeliveryInformationResponse> createDeliveryAddress(CreateDeliveryInformationRequest request);

    @GET("users/delivery-addresses")
    Call<DeliveryInformationListResponse> getDeliveryAddresses(@Header("Authorization") String token);

    // ──────── Products ────────

    @GET("products")
    Call<ProductResponse> getProducts(@QueryMap Map<String, Object> request);

    @GET("products/{id}")
    Call<ProductDetailResponse> getProductById(@Path("id") String productId);


    // ──────── Cart ────────

    @GET("carts")
    Call<CartResponse> getCart(@Header("Authorization") String token);

    @POST("carts/details")
    Call<CartResponse> addToCart(@Header("Authorization") String token, @Body CreateCartItemRequest request);

    @PATCH("carts/details/{id}")
    Call<CartResponse> updateCart(
            @Header("Authorization") String token,
            @Path("id") String id,
            @Body UpdateCartItemRequest request
    );

    @DELETE("carts/details/{id}")
    Call<CartResponse> removeFromCart(@Header("Authorization") String token, @Path("id") String id);


    // ──────── Orders ────────

    @GET("orders")
    Call<OrderResponse> getOrders(
        @Header("Authorization") String token,
        @QueryMap Map<String, Object> queryParams
    );

    @GET("orders/{id}")
    Call<SingleOrderResponse> getOrderById(@Header("Authorization") String token, @Path("id") String orderId);

    @POST("orders")
    Call<SingleOrderResponse> placeOrder(@Header("Authorization") String token, @Body CreateOrderRequest request);


    // ──────── Categories & Brands ────────

    @GET("categories")
    Call<CategoryResponse> getCategories();

    @GET("brands")
    Call<BrandResponse> getBrands();
}