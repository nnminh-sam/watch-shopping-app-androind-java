package nnminh.android.watchstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import nnminh.android.watchstore.R;
import nnminh.android.watchstore.models.OrderItem;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private List<OrderItem> items;

    public OrderItemAdapter(List<OrderItem> items) {
        this.items = items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        TextView textProduct, textPrice, textTotal;
        ImageView imageProduct;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textProduct = itemView.findViewById(R.id.textProduct);
            textPrice = itemView.findViewById(R.id.textPrice);
            textTotal = itemView.findViewById(R.id.textTotal);
            imageProduct = itemView.findViewById(R.id.imageProduct);
        }

        public void bind(OrderItem item) {
            textProduct.setText(item.getName());
            textPrice.setText(String.format("₫ %,d", (long)item.getPrice()));
            textTotal.setText("(x" + item.getQuantity() + ") " + String.format("₫ %,d", (long)(item.getPrice() * item.getQuantity())));
            Glide.with(itemView.getContext())
                    .load((item.getAsset() != null) ? item.getAsset() : null)
                    .placeholder(R.drawable.ic_watch_placeholder)
                    .into(imageProduct);
        }
    }
}