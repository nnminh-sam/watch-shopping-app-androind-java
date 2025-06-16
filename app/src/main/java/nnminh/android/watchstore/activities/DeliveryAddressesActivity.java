package nnminh.android.watchstore.activities;

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

import java.util.ArrayList;
import java.util.List;

import nnminh.android.watchstore.R;
import nnminh.android.watchstore.adapters.DeliveryAddressAdapter;
import nnminh.android.watchstore.auth.TokenManager;
import nnminh.android.watchstore.models.DeliveryInformation;
import nnminh.android.watchstore.models.DeliveryInformationListResponse;
import nnminh.android.watchstore.network.ApiClient;
import nnminh.android.watchstore.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryAddressesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textError;
    private Button buttonAddAddress;
    private DeliveryAddressAdapter adapter;
    private List<DeliveryInformation> addressList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_addresses);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Delivery Addresses");

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewAddresses);
        progressBar = findViewById(R.id.progressBar);
        textError = findViewById(R.id.textError);
        buttonAddAddress = findViewById(R.id.buttonAddAddress);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeliveryAddressAdapter(addressList);
        recyclerView.setAdapter(adapter);

        // Setup click listeners
        buttonAddAddress.setOnClickListener(v -> {
            // TODO: Implement add new address functionality
            Toast.makeText(this, "Add new address feature coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Load addresses
        loadAddresses();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadAddresses() {
        showLoading(true);
        String token = TokenManager.getInstance(this).getToken();
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

        apiService.getDeliveryAddresses(token).enqueue(new Callback<DeliveryInformationListResponse>() {
            @Override
            public void onResponse(Call<DeliveryInformationListResponse> call, Response<DeliveryInformationListResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    addressList.clear();
                    addressList.addAll(response.body().getDeliveryAddresses());
                    adapter.notifyDataSetChanged();
                    textError.setVisibility(addressList.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    showError("Failed to load addresses");
                }
            }

            @Override
            public void onFailure(Call<DeliveryInformationListResponse> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        textError.setText(message);
        textError.setVisibility(View.VISIBLE);
    }
} 