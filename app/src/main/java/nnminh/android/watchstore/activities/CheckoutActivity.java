package nnminh.android.watchstore.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import nnminh.android.watchstore.R;
import nnminh.android.watchstore.adapters.CheckoutProductAdapter;
import nnminh.android.watchstore.auth.TokenManager;
import nnminh.android.watchstore.models.CartItem;
import nnminh.android.watchstore.models.CreateOrderRequest;
import nnminh.android.watchstore.models.DeliveryInformation;
import nnminh.android.watchstore.models.DeliveryInformationListResponse;
import nnminh.android.watchstore.models.SingleOrderResponse;
import nnminh.android.watchstore.network.ApiClient;
import nnminh.android.watchstore.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import org.json.JSONObject;

public class CheckoutActivity extends AppCompatActivity {
    private RecyclerView recyclerSelectedItems;
    private RadioGroup radioGroupAddresses;
    private MaterialCardView cardNewAddress;
    private TextInputEditText editTextFullName, editTextPhone, editTextCity, 
                             editTextDistrict, editTextStreet, editTextSpecificAddress;
    private TextView textTotal;
    private Button buttonAddNewAddress, buttonConfirmOrder;

    private CheckoutProductAdapter adapter;
    private List<CartItem> selectedItems;
    private List<DeliveryInformation> deliveryAddresses;
    private String selectedAddressId;
    private long totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize views
        initializeViews();
        
        // Get selected items from intent
        selectedItems = getIntent().getParcelableArrayListExtra("selected_items");
        if (selectedItems == null) {
            Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup RecyclerView
        setupRecyclerView();

        // Load delivery addresses
        loadDeliveryAddresses();

        // Setup click listeners
        setupClickListeners();

        // Calculate and display total
        calculateTotal();
    }

    private void initializeViews() {
        recyclerSelectedItems = findViewById(R.id.recyclerSelectedItems);
        radioGroupAddresses = findViewById(R.id.radioGroupAddresses);
        cardNewAddress = findViewById(R.id.cardNewAddress);
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextCity = findViewById(R.id.editTextCity);
        editTextDistrict = findViewById(R.id.editTextDistrict);
        editTextStreet = findViewById(R.id.editTextStreet);
        editTextSpecificAddress = findViewById(R.id.editTextSpecificAddress);
        textTotal = findViewById(R.id.textTotal);
        buttonAddNewAddress = findViewById(R.id.buttonAddNewAddress);
        buttonConfirmOrder = findViewById(R.id.buttonConfirmOrder);

        // Initially hide new address form
        cardNewAddress.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        adapter = new CheckoutProductAdapter(selectedItems);
        recyclerSelectedItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerSelectedItems.setAdapter(adapter);
    }

    private void loadDeliveryAddresses() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        String token = TokenManager.getInstance(this).getToken();
        apiService.getDeliveryAddresses(token).enqueue(new Callback<DeliveryInformationListResponse>() {
            @Override
            public void onResponse(Call<DeliveryInformationListResponse> call, Response<DeliveryInformationListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    deliveryAddresses = response.body().getDeliveryAddresses();
                    populateAddressRadioGroup();
                }
            }

            @Override
            public void onFailure(Call<DeliveryInformationListResponse> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Failed to load addresses", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateAddressRadioGroup() {
        radioGroupAddresses.removeAllViews();
        for (DeliveryInformation address : deliveryAddresses) {
            View addressView = getLayoutInflater().inflate(R.layout.item_delivery_address, radioGroupAddresses, false);
            
            RadioButton radioButton = addressView.findViewById(R.id.radioButton);
            TextView textFullName = addressView.findViewById(R.id.textFullName);
            TextView textPhone = addressView.findViewById(R.id.textPhone);
            TextView textAddress = addressView.findViewById(R.id.textAddress);
            
            textFullName.setText(address.getFull_name());
            textPhone.setText(address.getPhone_number());
            textAddress.setText(String.format("%s, %s, %s, %s", 
                address.getCity(), address.getDistrict(), 
                address.getStreet(), address.getSpecific_address()));

            radioGroupAddresses.addView(addressView);
            
            // Set click listener for the entire view
            addressView.setOnClickListener(v -> {
                // Uncheck all radio buttons
                for (int i = 0; i < radioGroupAddresses.getChildCount(); i++) {
                    View child = radioGroupAddresses.getChildAt(i);
                    RadioButton rb = child.findViewById(R.id.radioButton);
                    rb.setChecked(false);
                }
                // Check the clicked radio button
                radioButton.setChecked(true);
                selectedAddressId = address.getId();
                cardNewAddress.setVisibility(View.GONE);
            });

            // Set click listener for the radio button
            radioButton.setOnClickListener(v -> {
                // Uncheck all radio buttons
                for (int i = 0; i < radioGroupAddresses.getChildCount(); i++) {
                    View child = radioGroupAddresses.getChildAt(i);
                    RadioButton rb = child.findViewById(R.id.radioButton);
                    rb.setChecked(false);
                }
                // Check the clicked radio button
                radioButton.setChecked(true);
                selectedAddressId = address.getId();
                cardNewAddress.setVisibility(View.GONE);
            });
        }
    }

    private void setupClickListeners() {
        buttonAddNewAddress.setOnClickListener(v -> {
            cardNewAddress.setVisibility(View.VISIBLE);
            selectedAddressId = null;
            // Clear selection in radio group
            for (int i = 0; i < radioGroupAddresses.getChildCount(); i++) {
                radioGroupAddresses.getChildAt(i).setSelected(false);
            }
        });

        buttonConfirmOrder.setOnClickListener(v -> {
            if (validateInput()) {
                placeOrder();
            }
        });
    }

    private boolean validateInput() {
        if (selectedAddressId == null) {
            // Validate new address form
            if (editTextFullName.getText().toString().trim().isEmpty() ||
                editTextPhone.getText().toString().trim().isEmpty() ||
                editTextCity.getText().toString().trim().isEmpty() ||
                editTextDistrict.getText().toString().trim().isEmpty() ||
                editTextStreet.getText().toString().trim().isEmpty() ||
                editTextSpecificAddress.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please fill in all address fields", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void calculateTotal() {
        totalAmount = 0;
        for (CartItem item : selectedItems) {
            totalAmount += item.getPrice() * item.getQuantity();
        }
        
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        formatter.setMaximumFractionDigits(0);
        textTotal.setText("Total: " + formatter.format(totalAmount) + " ₫");
    }

    private void placeOrder() {
        List<String> cartDetailIds = new ArrayList<>();
        for (CartItem item : selectedItems) {
            cartDetailIds.add(item.getId());
        }

        CreateOrderRequest request;
        if (selectedAddressId != null) {
            request = new CreateOrderRequest(
                cartDetailIds,
                "CASH", // Default payment method
                selectedAddressId,
                null
            );
        } else {
            // Create new delivery information
            DeliveryInformation newAddress = new DeliveryInformation(
                null,
                editTextFullName.getText().toString().trim(),
                editTextCity.getText().toString().trim(),
                editTextDistrict.getText().toString().trim(),
                editTextStreet.getText().toString().trim(),
                editTextSpecificAddress.getText().toString().trim(),
                editTextPhone.getText().toString().trim(),
                "false"
            );
            request = new CreateOrderRequest(
                cartDetailIds,
                "CASH",
                null,
                newAddress
            );
        }

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.placeOrder(null, request).enqueue(new Callback<SingleOrderResponse>() {
            @Override
            public void onResponse(Call<SingleOrderResponse> call, Response<SingleOrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(CheckoutActivity.this, "Order placed successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMessage = "Failed to place order";
                    if (response.errorBody() != null) {
                        try {
                            JSONObject errorJson = new JSONObject(response.errorBody().string());
                            errorMessage = errorJson.optString("message", errorMessage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(CheckoutActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SingleOrderResponse> call, Throwable t) {
                Toast.makeText(CheckoutActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
