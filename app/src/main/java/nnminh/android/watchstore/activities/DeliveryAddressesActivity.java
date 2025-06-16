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

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import nnminh.android.watchstore.R;
import nnminh.android.watchstore.adapters.DeliveryAddressAdapter;
import nnminh.android.watchstore.auth.TokenManager;
import nnminh.android.watchstore.models.CreateDeliveryInformationRequest;
import nnminh.android.watchstore.models.DeliveryInformation;
import nnminh.android.watchstore.models.DeliveryInformationListResponse;
import nnminh.android.watchstore.models.DeliveryInformationResponse;
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
        buttonAddAddress.setOnClickListener(v -> showAddAddressDialog());

        // Load addresses
        loadAddresses();
    }

    private void showAddAddressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_address, null);
        builder.setView(dialogView);

        TextInputEditText editTextFullName = dialogView.findViewById(R.id.editTextFullName);
        TextInputEditText editTextPhone = dialogView.findViewById(R.id.editTextPhone);
        TextInputEditText editTextCity = dialogView.findViewById(R.id.editTextCity);
        TextInputEditText editTextDistrict = dialogView.findViewById(R.id.editTextDistrict);
        TextInputEditText editTextStreet = dialogView.findViewById(R.id.editTextStreet);
        TextInputEditText editTextSpecificAddress = dialogView.findViewById(R.id.editTextSpecificAddress);

        builder.setTitle("Add New Address")
                .setPositiveButton("Add", (dialog, which) -> {
                    String fullName = editTextFullName.getText().toString().trim();
                    String phone = editTextPhone.getText().toString().trim();
                    String city = editTextCity.getText().toString().trim();
                    String district = editTextDistrict.getText().toString().trim();
                    String street = editTextStreet.getText().toString().trim();
                    String specificAddress = editTextSpecificAddress.getText().toString().trim();

                    if (validateAddressInput(fullName, phone, city, district, street, specificAddress)) {
                        createNewAddress(fullName, phone, city, district, street, specificAddress);
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean validateAddressInput(String fullName, String phone, String city, 
                                       String district, String street, String specificAddress) {
        if (fullName.isEmpty() || phone.isEmpty() || city.isEmpty() || 
            district.isEmpty() || street.isEmpty() || specificAddress.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createNewAddress(String fullName, String phone, String city, 
                                String district, String street, String specificAddress) {
        showLoading(true);
        String token = TokenManager.getInstance(this).getToken();
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

        CreateDeliveryInformationRequest request = new CreateDeliveryInformationRequest(
            fullName,
            phone,
            city,
            district,
            street,
            specificAddress,
            false
        );

        apiService.createDeliveryAddress(token, request).enqueue(new Callback<DeliveryInformationResponse>() {
            @Override
            public void onResponse(Call<DeliveryInformationResponse> call, Response<DeliveryInformationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(DeliveryAddressesActivity.this, "Address added successfully", Toast.LENGTH_SHORT).show();
                    loadAddresses(); // Reload the addresses list
                } else {
                    showLoading(false);
                    Toast.makeText(DeliveryAddressesActivity.this, "Failed to add address", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeliveryInformationResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(DeliveryAddressesActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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