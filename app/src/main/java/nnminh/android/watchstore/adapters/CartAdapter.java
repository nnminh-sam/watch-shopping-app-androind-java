package nnminh.android.watchstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.NumberFormat;
import java.util.*;

import nnminh.android.watchstore.R;
import nnminh.android.watchstore.models.CartItem;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface CartItemListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onRemoveItem(CartItem item);
    }
    public interface SelectionChangedListener {
        void onSelectionChanged(Set<String> selectedIds);
    }

    private List<CartItem> cartItems;
    private final CartItemListener listener;
    private final SelectionChangedListener selectionChangedListener;
    private Set<String> selectedProductIds = new HashSet<>();

    public CartAdapter(List<CartItem> cartItems, CartItemListener listener, SelectionChangedListener selectionChangedListener) {
        this.cartItems = cartItems;
        this.listener = listener;
        this.selectionChangedListener = selectionChangedListener;
    }

    public void setCartItems(List<CartItem> items) {
        this.cartItems = items != null ? items : new ArrayList<>();
        // By default, select all loaded items
        selectedProductIds.clear();
        for (CartItem item : cartItems) {
            selectedProductIds.add(item.getProduct_id());
        }
        notifyDataSetChanged();
        if (selectionChangedListener != null) selectionChangedListener.onSelectionChanged(selectedProductIds);
    }

    public Set<String> getSelectedProductIds() {
        return selectedProductIds;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(cartItems.get(position), selectedProductIds.contains(cartItems.get(position).getProduct_id()), isAllSelected());
        holder.setCheckListener((isChecked, item) -> {
            String id = item.getProduct_id();
            if (isChecked) selectedProductIds.add(id);
            else selectedProductIds.remove(id);
            if (selectionChangedListener != null) selectionChangedListener.onSelectionChanged(selectedProductIds);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public boolean isAllSelected() {
        return selectedProductIds.size() == cartItems.size();
    }

    public void setAllSelected(boolean selectAll) {
        selectedProductIds.clear();
        if (selectAll) {
            for (CartItem item : cartItems) {
                selectedProductIds.add(item.getProduct_id());
            }
        }
        notifyDataSetChanged();
        if (selectionChangedListener != null) {
            selectionChangedListener.onSelectionChanged(selectedProductIds);
        }
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textName, textPrice, textQuantity;
        ImageButton buttonPlus, buttonMinus, buttonRemove;
        CheckBox checkSelect;
        CartItemListener listener;
        CartItem item;

        public interface CheckListener { void onCheckChanged(boolean checked, CartItem item); }
        private CheckListener checkListener;

        public CartViewHolder(@NonNull View itemView, CartItemListener listener) {
            super(itemView);
            this.listener = listener;
            checkSelect = itemView.findViewById(R.id.checkSelect);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textName = itemView.findViewById(R.id.textName);
            textPrice = itemView.findViewById(R.id.textPrice);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
        }

        void bind(CartItem item, boolean isChecked, boolean isAllChecked) {
            this.item = item;
            textName.setText(item.getName());
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            formatter.setMaximumFractionDigits(0);
            textPrice.setText(formatter.format(item.getPrice()) + " ₫");
            textQuantity.setText(String.valueOf(item.getQuantity()));
            Glide.with(itemView.getContext())
                    .load((item.getAsset() != null) ? item.getAsset() : null)
                    .placeholder(R.drawable.ic_watch_placeholder)
                    .into(imageProduct);

            // Checkbox
            checkSelect.setOnCheckedChangeListener(null); // avoid unwanted triggering
            checkSelect.setChecked(isChecked);
            checkSelect.setOnCheckedChangeListener((buttonView, checked) -> {
                if (checkListener != null) checkListener.onCheckChanged(checked, item);
            });

            buttonPlus.setOnClickListener(v -> {
                int q = item.getQuantity() + 1;
                listener.onQuantityChanged(item, q);
            });
            buttonMinus.setOnClickListener(v -> {
                int q = Math.max(1, item.getQuantity() - 1);
                if (q != item.getQuantity()) {
                    listener.onQuantityChanged(item, q);
                }
            });
            buttonRemove.setOnClickListener(v -> listener.onRemoveItem(item));
        }
        void setCheckListener(CheckListener l) { this.checkListener = l; }
    }
}