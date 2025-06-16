package nnminh.android.watchstore.activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nnminh.android.watchstore.R;
import nnminh.android.watchstore.adapters.OrderItemAdapter;
import nnminh.android.watchstore.auth.TokenManager;
import nnminh.android.watchstore.models.Order;
import nnminh.android.watchstore.models.OrderItem;
import nnminh.android.watchstore.models.SingleOrderResponse;
import nnminh.android.watchstore.network.ApiClient;
import nnminh.android.watchstore.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private TextView textOrderId;
    private TextView textOrderDate;
    private TextView textOrderStatus;
    private TextView textOrderTotal;
    private TextView textDeliveryName;
    private TextView textPhoneNumber;
    private TextView textOrderAddress;
    private Button buttonCancelOrder;
    private ProgressBar progressBar;
    private TextView textError;
    private RecyclerView recyclerViewItems;
    private OrderItemAdapter itemAdapter;
    private List<OrderItem> orderItems = new ArrayList<>();
    private String orderId;
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Order Detail");

        // Initialize views
        textOrderId = findViewById(R.id.textOrderId);
        textOrderDate = findViewById(R.id.textOrderDate);
        textOrderStatus = findViewById(R.id.textOrderStatus);
        textOrderTotal = findViewById(R.id.textOrderTotal);
        textDeliveryName = findViewById(R.id.textDeliveryName);
        textPhoneNumber = findViewById(R.id.textPhoneNumber);
        textOrderAddress = findViewById(R.id.textOrderAddress);
        buttonCancelOrder = findViewById(R.id.buttonCancelOrder);
        progressBar = findViewById(R.id.progressBar);
        textError = findViewById(R.id.textError);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);

        // Setup RecyclerView
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new OrderItemAdapter(orderItems);
        recyclerViewItems.setAdapter(itemAdapter);

        // Get order ID from intent
        orderId = getIntent().getStringExtra("order_id");
        if (orderId == null) {
            showError("Order ID not found");
            return;
        }

        // Setup cancel button click listener
        buttonCancelOrder.setOnClickListener(v -> showCancelConfirmationDialog());

        // Load order details
        loadOrderDetails();
    }

    private void showCancelConfirmationDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Cancel Order")
            .setMessage("Are you sure you want to cancel this order?")
            .setPositiveButton("Yes", (dialog, which) -> cancelOrder())
            .setNegativeButton("No", null)
            .show();
    }

    private void cancelOrder() {
        showLoading(true);
        String token = TokenManager.getInstance(this).getToken();
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

        apiService.updateOrderStatus(token, orderId, "CANCELED").enqueue(new Callback<SingleOrderResponse>() {
            @Override
            public void onResponse(Call<SingleOrderResponse> call, Response<SingleOrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(OrderDetailActivity.this, "Order cancelled successfully", Toast.LENGTH_SHORT).show();
                    loadOrderDetails(); // Reload order details
                } else {
                    showLoading(false);
                    Toast.makeText(OrderDetailActivity.this, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SingleOrderResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(OrderDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadOrderDetails() {
        showLoading(true);
        String token = TokenManager.getInstance(this).getToken();
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

        apiService.getOrderById(token, orderId).enqueue(new Callback<SingleOrderResponse>() {
            @Override
            public void onResponse(Call<SingleOrderResponse> call, Response<SingleOrderResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    currentOrder = response.body().getOrder();
                    updateUI();
                } else {
                    showError("Failed to load order details");
                }
            }

            @Override
            public void onFailure(Call<SingleOrderResponse> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void updateUI() {
        if (currentOrder == null) return;

        // Update order items
        orderItems.clear();
        orderItems.addAll(currentOrder.getDetails());
        itemAdapter.notifyDataSetChanged();

        // Show/hide cancel button based on order status
        boolean canCancel = "PENDING".equals(currentOrder.getStatus()) || "PROCESSING".equals(currentOrder.getStatus());
        buttonCancelOrder.setVisibility(canCancel ? View.VISIBLE : View.GONE);

        textOrderId.setText("ORDER #" + currentOrder.getOrder_number().toUpperCase());

        // Format date
        String dateStr = currentOrder.getCreated_at().toString();
        try {
            SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            Date date = iso.parse(currentOrder.getCreated_at().toString());
            if (date != null) {
                SimpleDateFormat out = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                textOrderDate.setText(out.format(date));
            }
        } catch (Exception e) {
            textOrderDate.setText(dateStr);
        }

        // Format total
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        nf.setMaximumFractionDigits(0);
        textOrderTotal.setText("Total: " + nf.format(currentOrder.getTotal()) + " ₫");

        textOrderStatus.setText(currentOrder.getStatus() != null ? currentOrder.getStatus() : "Unknown");

        if (currentOrder.getDelivery_information() != null) {
            textDeliveryName.setText("Receiver: " + currentOrder.getDelivery_information().getFull_name());
            textPhoneNumber.setText("Phone number: " + currentOrder.getDelivery_information().getPhone_number());
            textOrderAddress.setText("Delivery address: " + currentOrder.getDelivery_information().getSpecific_address());
        } else {
            textDeliveryName.setText("Receiver: John Doe (This is sample delivery information)");
            textPhoneNumber.setText("Phone number: 0123456789 (This is sample delivery information)");
            textOrderAddress.setText("Delivery address: 123 Street, City, Country (This is sample delivery information)");
        }
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        recyclerViewItems.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        textError.setText(message);
        textError.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}