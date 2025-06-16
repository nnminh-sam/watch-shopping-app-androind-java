package nnminh.android.watchstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import nnminh.android.watchstore.R;
import nnminh.android.watchstore.activities.ProductDetailActivity;
import nnminh.android.watchstore.models.Brand;
import nnminh.android.watchstore.models.Category;
import nnminh.android.watchstore.models.Product;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_PRODUCT = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    private final Context context;
    private List<Product> productList;
    private boolean showFooter;
    private int currentPage = 1, totalPages = 1;
    private PaginationListener paginationListener;

    public interface PaginationListener {
        void onPrev();
        void onNext();
    }

    public ProductAdapter(Context context, List<Product> productList, boolean showFooter,
                          int currentPage, int totalPages, PaginationListener listener) {
        this.context = context;
        this.productList = productList != null ? productList : new ArrayList<>();
        this.showFooter = showFooter;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.paginationListener = listener;
    }

    public void update(List<Product> products, int currentPage, int totalPages, boolean showFooter) {
        this.productList = products != null ? products : new ArrayList<>();
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.showFooter = showFooter;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (showFooter && position == getItemCount() - 1) {
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_PRODUCT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pagination_footer, parent, false);
            return new FooterViewHolder(view, paginationListener);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
            return new ProductViewHolder(view, context);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_PRODUCT) {
            ((ProductViewHolder) holder).bind(productList.get(position));
        } else if (getItemViewType(position) == VIEW_TYPE_FOOTER) {
            ((FooterViewHolder) holder).bind(currentPage, totalPages);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size() + (showFooter ? 1 : 0);
    }

    // ------------------------------
    // Product ViewHolder (with click)
    // ------------------------------
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textName, textBrand, textCategories, textPrice, textSold, textAvailability;
        Context context;

        ProductViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textName = itemView.findViewById(R.id.textName);
            textBrand = itemView.findViewById(R.id.textBrand);
            textCategories = itemView.findViewById(R.id.textCategories);
            textPrice = itemView.findViewById(R.id.textPrice);
            textSold = itemView.findViewById(R.id.textSold);
            textAvailability = itemView.findViewById(R.id.textAvailability);
        }

        public void bind(Product product) {
            textName.setText(product.getName());
            // Brand
            Brand brand = product.getBrand();
            textBrand.setText(brand != null ? brand.getName() : "Unknown Brand");
            // Categories
            String categoriesText = "";
            if (product.getCategories() != null && !product.getCategories().isEmpty()) {
                List<String> categoryNames = new ArrayList<>();
                for (Category c : product.getCategories()) {
                    if (c != null && c.getName() != null)
                        categoryNames.add(c.getName());
                }
                categoriesText = android.text.TextUtils.join(", ", categoryNames);
            }
            textCategories.setText(categoriesText.isEmpty() ? "No category" : categoriesText);

            // Price VND
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            formatter.setMaximumFractionDigits(0);
            String priceStr = formatter.format(product.getPrice()) + " ₫";
            textPrice.setText(priceStr);

            // Sold
            textSold.setText("Sold: " + product.getSold());

            // Availability badge
            if (product.getStock() > 0) {
                textAvailability.setText("In stock");
                textAvailability.setBackgroundResource(R.drawable.bg_availability_badge);
                textAvailability.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
            } else {
                textAvailability.setText("Out of stock");
                textAvailability.setBackgroundResource(R.drawable.bg_availability_badge);
                textAvailability.setTextColor(Color.RED);
            }

            // Product image
            String imageUrl = null;
            if (product.getAssets() != null && !product.getAssets().isEmpty()) {
                imageUrl = product.getAssets().get(0);
            }
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_watch_placeholder)
                    .into(imageProduct);

            // Product click listener: Open ProductDetailActivity
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                context.startActivity(intent);
            });
        }
    }

    // --------------------------
    // Footer ViewHolder (paging)
    // --------------------------
    static class FooterViewHolder extends RecyclerView.ViewHolder {
        ImageButton buttonPrevPage, buttonNextPage;
        TextView textPageInfo;

        public FooterViewHolder(@NonNull View itemView, PaginationListener listener) {
            super(itemView);
            buttonPrevPage = itemView.findViewById(R.id.buttonPrevPage);
            buttonNextPage = itemView.findViewById(R.id.buttonNextPage);
            textPageInfo = itemView.findViewById(R.id.textPageInfo);

            buttonPrevPage.setOnClickListener(v -> {
                if (listener != null) listener.onPrev();
            });
            buttonNextPage.setOnClickListener(v -> {
                if (listener != null) listener.onNext();
            });
        }

        public void bind(int currentPage, int totalPages) {
            textPageInfo.setText("Page " + currentPage + " / " + totalPages);
            buttonPrevPage.setEnabled(currentPage > 1);
            buttonNextPage.setEnabled(currentPage < totalPages);
        }
    }
}