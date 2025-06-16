package nnminh.android.watchstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import nnminh.android.watchstore.R;
import nnminh.android.watchstore.models.CartItem;

public class CheckoutProductAdapter extends RecyclerView.Adapter<CheckoutProductAdapter.ViewHolder> {
    private final List<CartItem> items;

    public CheckoutProductAdapter(List<CartItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkout_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textProductName, textPrice, textQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textProductName = itemView.findViewById(R.id.textProductName);
            textPrice = itemView.findViewById(R.id.textPrice);
            textQuantity = itemView.findViewById(R.id.textQuantity);
        }

        public void bind(CartItem item) {
            textProductName.setText(item.getName());
            
            // Format price
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            formatter.setMaximumFractionDigits(0);
            textPrice.setText(formatter.format(item.getPrice()) + " ₫");
            
            // Format quantity
            textQuantity.setText("Quantity: " + item.getQuantity());

            // Load product image
            Glide.with(itemView.getContext())
                    .load(item.getAsset())
                    .placeholder(R.drawable.ic_watch_placeholder)
                    .into(imageProduct);
        }
    }
} 