package nnminh.android.watchstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import nnminh.android.watchstore.R;
import nnminh.android.watchstore.models.Order;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders = new ArrayList<>();
    private OnOrderClickListener listener;


    public OrderAdapter(List<Order> orders, OnOrderClickListener listener) {
        if (orders != null) this.orders = orders;
        this.listener = listener;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders != null ? orders : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orders.get(position));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClick(orders.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textOrderId, textOrderDate, textOrderTotal, textOrderStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textOrderId = itemView.findViewById(R.id.textOrderId);
            textOrderDate = itemView.findViewById(R.id.textOrderDate);
            textOrderTotal = itemView.findViewById(R.id.textOrderTotal);
            textOrderStatus = itemView.findViewById(R.id.textOrderStatus);
        }

        public void bind(Order order) {
            textOrderId.setText("Order #" + order.getOrder_number());

            // Format date
            Date date = order.getCreated_at();
            String dateStr = "";
            if (date != null) {
                SimpleDateFormat out = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                dateStr = out.format(date);
            }
            textOrderDate.setText(dateStr);

            // Format total
            NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
            nf.setMaximumFractionDigits(0);
            textOrderTotal.setText("Total: " + nf.format(order.getTotal()) + " ₫");

            textOrderStatus.setText(order.getStatus() != null ? order.getStatus() : "Unknown");
        }
    }

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }
}