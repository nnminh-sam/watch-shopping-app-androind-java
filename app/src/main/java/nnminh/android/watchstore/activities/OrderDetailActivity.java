package nnminh.android.watchstore.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import nnminh.android.watchstore.R;
import nnminh.android.watchstore.adapters.OrderItemAdapter;
import nnminh.android.watchstore.models.Order;
import nnminh.android.watchstore.models.SingleOrderResponse;
import nnminh.android.watchstore.network.ApiClient;
import nnminh.android.watchstore.network.ApiService;
import nnminh.android.watchstore.auth.TokenManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView textOrderId, textOrderDate, textOrderTotal, textOrderStatus, textError;
    private TextView textDeliveryName, textPhoneNumber, textOrderAddress;
    private RecyclerView recyclerViewItems;
    private ProgressBar progressBar;
    private OrderItemAdapter itemAdapter;
    private ImageButton buttonBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        textOrderId = findViewById(R.id.textOrderId);
        textOrderDate = findViewById(R.id.textOrderDate);
        textOrderTotal = findViewById(R.id.textOrderTotal);
        textOrderStatus = findViewById(R.id.textOrderStatus);
        textDeliveryName = findViewById(R.id.textDeliveryName);
        textPhoneNumber = findViewById(R.id.textPhoneNumber);
        textOrderAddress = findViewById(R.id.textOrderAddress);
        textError = findViewById(R.id.textError);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        progressBar = findViewById(R.id.progressBar);

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());

        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new OrderItemAdapter(new ArrayList<>());
        recyclerViewItems.setAdapter(itemAdapter);

        String orderId = getIntent().getStringExtra("order_id");
        if (orderId == null) {
            textError.setVisibility(View.VISIBLE);
            textError.setText("No order ID found!");
            return;
        }

        loadOrderDetail(orderId);
    }

    private void loadOrderDetail(String orderId) {
        showLoading(true);
        String token = TokenManager.getInstance(this).getToken();
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.getOrderById(token, orderId).enqueue(new Callback<SingleOrderResponse>() {
            @Override
            public void onResponse(Call<SingleOrderResponse> call, Response<SingleOrderResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().getOrder() != null) {
                    bindOrderDetail(response.body().getOrder());
                } else {
                    textError.setVisibility(View.VISIBLE);
                    textError.setText("Failed to load order detail.");
                }
            }

            @Override
            public void onFailure(Call<SingleOrderResponse> call, Throwable t) {
                showLoading(false);
                textError.setVisibility(View.VISIBLE);
                textError.setText("Network error.");
            }
        });
    }

    private void bindOrderDetail(Order order) {
        textOrderId.setText("ORDER #" + order.getOrder_number().toUpperCase());

        // Format date
        String dateStr = order.getCreated_at().toString();
        try {
            SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            Date date = iso.parse(order.getCreated_at().toString());
            if (date != null) {
                SimpleDateFormat out = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                dateStr = out.format(date);
            }
        } catch (Exception ignored) {}
        textOrderDate.setText(dateStr);

        // Format total
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        nf.setMaximumFractionDigits(0);
        textOrderTotal.setText("Total: " + nf.format(order.getTotal()) + " ₫");

        textOrderStatus.setText(order.getStatus() != null ? order.getStatus() : "Unknown");

        if (order.getDelivery_information() != null) {
            textDeliveryName.setText("Receiver: " + order.getDelivery_information().getFull_name());
            textPhoneNumber.setText("Phone number: " + order.getDelivery_information().getPhone_number());
            textOrderAddress.setText("Delivery address: " + order.getDelivery_information().getSpecific_address());
        } else {
            textDeliveryName.setText("Receiver: John Doe (This is sample delivery information)");
            textPhoneNumber.setText("Phone number: 0123456789 (This is sample delivery information)");
            textOrderAddress.setText("Delivery address: 123 Street, City, Country (This is sample delivery information)");
        }

        if (order.getDetails() != null)
            itemAdapter.setItems(order.getDetails());
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}