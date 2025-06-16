package nnminh.android.watchstore.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

import nnminh.android.watchstore.R;
import nnminh.android.watchstore.auth.TokenManager;
import nnminh.android.watchstore.models.CartResponse;
import nnminh.android.watchstore.models.CreateCartItemRequest;
import nnminh.android.watchstore.models.Product;
import nnminh.android.watchstore.models.ProductDetailResponse;
import nnminh.android.watchstore.network.ApiClient;
import nnminh.android.watchstore.network.ApiService;
import nnminh.android.watchstore.utils.CartBadgeHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    private ImageButton buttonBack, buttonAddToCart;
    private ProgressBar progressBar;
    private TextView textName, textBrand, textPrice, textSold, textCategories, textDesc, textError;
    private RecyclerView recyclerViewImages;
    private ImagesAdapter imagesAdapter;
    private ImageButton buttonMinusQuantity, buttonPlusQuantity;
    private TextView textQuantity;
    private int currentQuantity = 1;

    private String productId;
    private Product product;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        productId = getIntent().getStringExtra("product_id");
        if (productId == null) {
            finish();
            return;
        }

        buttonBack = findViewById(R.id.buttonBack);
        buttonAddToCart = findViewById(R.id.buttonAddToCart);
        progressBar = findViewById(R.id.progressBar);
        textName = findViewById(R.id.textName);
        textBrand = findViewById(R.id.textBrand);
        textPrice = findViewById(R.id.textPrice);
        textSold = findViewById(R.id.textSold);
        textCategories = findViewById(R.id.textCategories);
        textDesc = findViewById(R.id.textDesc);
        textError = findViewById(R.id.textError);
        recyclerViewImages = findViewById(R.id.recyclerViewImages);
        buttonMinusQuantity = findViewById(R.id.buttonMinusQuantity);
        buttonPlusQuantity = findViewById(R.id.buttonPlusQuantity);
        textQuantity = findViewById(R.id.textQuantity);

        buttonMinusQuantity.setOnClickListener(v -> {
            if (currentQuantity > 1) {
                currentQuantity--;
                textQuantity.setText(String.valueOf(currentQuantity));
            }
        });
        buttonPlusQuantity.setOnClickListener(v -> {
            currentQuantity++;
            textQuantity.setText(String.valueOf(currentQuantity));
        });
        textQuantity.setText(String.valueOf(currentQuantity));

        imagesAdapter = new ImagesAdapter(new ArrayList<>());
        recyclerViewImages.setAdapter(imagesAdapter);
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        buttonBack.setOnClickListener(v -> finish());

        buttonAddToCart.setOnClickListener(v -> {
            if (product != null) {
                addProductToCart(product.getId(), currentQuantity);
            }
        });

        loadProductDetail();
    }

    private void addProductToCart(String productId, int quantity) {
        buttonAddToCart.setEnabled(false);
        ProgressBar progress = new ProgressBar(this);
        progress.setVisibility(View.VISIBLE);

        String token = TokenManager.getInstance(this).getToken();
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        CreateCartItemRequest requestBody = new CreateCartItemRequest(productId, quantity);
        apiService.addToCart(token, requestBody).enqueue(new retrofit2.Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                buttonAddToCart.setEnabled(true);
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ProductDetailActivity.this, "Added to cart!", Toast.LENGTH_SHORT).show();
                    CartBadgeHelper.updateCartBadge(ProductDetailActivity.this);
                } else {
                    Toast.makeText(ProductDetailActivity.this, "Failed to add to cart.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                buttonAddToCart.setEnabled(true);
                progress.setVisibility(View.GONE);
                Toast.makeText(ProductDetailActivity.this, "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductDetail() {
        showLoading(true);
        textError.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.getProductById(productId).enqueue(new Callback<ProductDetailResponse>() {
            @Override
            public void onResponse(Call<ProductDetailResponse> call, Response<ProductDetailResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    product = response.body().getProduct();
                    bindProduct(product);
                } else {
                    showError("Failed to load product.");
                }
            }

            @Override
            public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
                showLoading(false);
                showError("Network error.");
            }
        });
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showError(String msg) {
        textError.setVisibility(View.VISIBLE);
        textError.setText(msg);
    }

    private void bindProduct(Product p) {
        textName.setText(p.getName());
        textBrand.setText(p.getBrand() != null ? p.getBrand().getName() : "");
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        formatter.setMaximumFractionDigits(0);
        textPrice.setText("Price: " + formatter.format(p.getPrice()) + " ₫");
        textSold.setText("Sold: " + p.getSold());

        if (p.getCategories() != null && !p.getCategories().isEmpty()) {
            List<String> catNames = new ArrayList<>();
            for (var cat : p.getCategories()) catNames.add(cat.getName());
            textCategories.setText("Categories: " + TextUtils.join(", ", catNames));
        } else {
            textCategories.setText("Categories: None");
        }

        textDesc.setText(p.getDescription() != null ? p.getDescription() : "No description.");

        imagesAdapter.setImages(p.getAssets() != null ? p.getAssets() : new ArrayList<>());
    }

    // Simple inner RecyclerView Adapter for image carousel
    static class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageVH> {
        private List<String> images;
        ImagesAdapter(List<String> images) { this.images = images; }
        void setImages(List<String> imgs) { this.images = imgs; notifyDataSetChanged(); }

        @NonNull
        @Override
        public ImageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImageView iv = new ImageView(parent.getContext());
            iv.setLayoutParams(new RecyclerView.LayoutParams(600, ViewGroup.LayoutParams.MATCH_PARENT));
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            iv.setPadding(8, 8, 8, 8);
            return new ImageVH(iv);
        }
        @Override
        public void onBindViewHolder(@NonNull ImageVH holder, int pos) {
            Glide.with(holder.itemView.getContext())
                    .load(images.get(pos))
                    .placeholder(R.drawable.ic_watch_placeholder)
                    .into((ImageView) holder.itemView);
        }
        @Override public int getItemCount() { return images != null ? images.size() : 0; }
        static class ImageVH extends RecyclerView.ViewHolder {
            ImageVH(@NonNull View itemView) { super(itemView); }
        }
    }
}