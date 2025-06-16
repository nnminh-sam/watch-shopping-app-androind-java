package nnminh.android.watchstore.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import nnminh.android.watchstore.R;
import nnminh.android.watchstore.activities.CheckoutActivity;
import nnminh.android.watchstore.adapters.CartAdapter;
import nnminh.android.watchstore.auth.TokenManager;
import nnminh.android.watchstore.models.CartItem;
import nnminh.android.watchstore.models.CartResponse;
import nnminh.android.watchstore.models.UpdateCartItemRequest;
import nnminh.android.watchstore.network.ApiClient;
import nnminh.android.watchstore.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.NumberFormat;
import java.util.*;

public class CartFragment extends Fragment implements CartAdapter.CartItemListener {
    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private ProgressBar progressBar;
    private TextView textTotal, textEmpty, textError;
    private Button buttonCheckout;
    private CheckBox checkboxSelectAll;

    private List<CartItem> cartItems = new ArrayList<>();
    private boolean updatingSelectAll = false; // Prevents recursive check event

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerViewCart = view.findViewById(R.id.recyclerViewCart);
        progressBar = view.findViewById(R.id.progressBar);
        textTotal = view.findViewById(R.id.textTotal);
        buttonCheckout = view.findViewById(R.id.buttonCheckout);
        textEmpty = view.findViewById(R.id.textEmptyCart);
        textError = view.findViewById(R.id.textError);
        checkboxSelectAll = view.findViewById(R.id.checkboxSelectAll);

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(cartItems, this, selectedIds -> {
            updateTotalForSelected(selectedIds);
            syncSelectAllCheckbox();
        });
        recyclerViewCart.setAdapter(cartAdapter);

        checkboxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (updatingSelectAll) return; // prevent recursion
            cartAdapter.setAllSelected(isChecked);
            updateTotalForSelected(cartAdapter.getSelectedProductIds());
        });

        buttonCheckout.setOnClickListener(v -> {
            Set<String> selectedIds = cartAdapter.getSelectedProductIds();
            List<CartItem> selectedItems = new ArrayList<>();
            for (CartItem item : cartItems) {
                if (selectedIds.contains(item.getProduct_id())) {
                    selectedItems.add(item);
                }
            }
            if (selectedItems.isEmpty()) {
                Toast.makeText(getContext(), "Please select items to checkout!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Launch CheckoutActivity with selected items
            Intent intent = new Intent(getContext(), CheckoutActivity.class);
            intent.putParcelableArrayListExtra("selected_items", new ArrayList<>(selectedItems));
            startActivity(intent);
        });

        fetchCart();
        return view;
    }

    private void fetchCart() {
        showLoading(true);
        String token = TokenManager.getInstance(getContext()).getToken();
        textError.setVisibility(View.GONE);
        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        apiService.getCart(token).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null && response.body().getCart().getDetails() != null) {
                    cartItems = response.body().getCart().getDetails();
                    cartAdapter.setCartItems(cartItems);
                    updateTotalForSelected(cartAdapter.getSelectedProductIds());
                    showEmpty(cartItems.isEmpty());
                    syncSelectAllCheckbox();
                } else {
                    showEmpty(true);
                }
            }
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                showLoading(false);
                textError.setVisibility(View.VISIBLE);
                textError.setText("Failed to load cart.");
                showEmpty(true);
            }
        });
    }

    private void updateTotalForSelected(Set<String> selectedIds) {
        long totalSelected = 0;
        for (CartItem item : cartItems) {
            if (selectedIds.contains(item.getProduct_id())) {
                totalSelected += item.getPrice() * item.getQuantity();
            }
        }
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        formatter.setMaximumFractionDigits(0);
        textTotal.setText("Total: " + formatter.format(totalSelected) + " ₫");
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showEmpty(boolean empty) {
        textEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        recyclerViewCart.setVisibility(empty ? View.GONE : View.VISIBLE);
        buttonCheckout.setVisibility(empty ? View.GONE : View.VISIBLE);
        checkboxSelectAll.setVisibility(empty ? View.GONE : View.VISIBLE);
        textTotal.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    // ==== CartAdapter.CartItemListener methods ====

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        showLoading(true);
        String token = TokenManager.getInstance(getContext()).getToken();
        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        UpdateCartItemRequest requestBody = new UpdateCartItemRequest(newQuantity);
        apiService.updateCart(token, item.getId(), requestBody).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    cartItems = response.body().getCart().getDetails();
                    cartAdapter.setCartItems(cartItems);
                    updateTotalForSelected(cartAdapter.getSelectedProductIds());
                    showEmpty(cartItems.isEmpty());
                    syncSelectAllCheckbox();
                } else {
                    Toast.makeText(getContext(), "Failed to update quantity", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRemoveItem(CartItem item) {
        showLoading(true);
        String token = TokenManager.getInstance(getContext()).getToken();
        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        apiService.removeFromCart(token, item.getId()).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    cartItems = response.body().getCart().getDetails();
                    cartAdapter.setCartItems(cartItems);
                    updateTotalForSelected(cartAdapter.getSelectedProductIds());
                    showEmpty(cartItems.isEmpty());
                    syncSelectAllCheckbox();
                } else {
                    Toast.makeText(getContext(), "Failed to remove item", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Keep the select all checkbox in sync with selection **/
    private void syncSelectAllCheckbox() {
        updatingSelectAll = true;
        if (cartAdapter != null && checkboxSelectAll != null) {
            checkboxSelectAll.setChecked(cartAdapter.isAllSelected());
        }
        updatingSelectAll = false;
    }
}