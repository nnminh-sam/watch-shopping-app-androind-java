package nnminh.android.watchstore.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nnminh.android.watchstore.R;
import nnminh.android.watchstore.models.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categories;
    private final OnCategoryClickListener listener;
    private int selectedPosition = 0; // Default select "All Categories"

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, int position);
    }

    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.textCategory.setText(category.getName());
        boolean isSelected = (position == selectedPosition);

        // Chip visual state
        int bgRes = isSelected ? R.drawable.bg_category_chip_selected : R.drawable.bg_category_chip;
        int textColor = ContextCompat.getColor(holder.itemView.getContext(),
                isSelected ? R.color.colorAccent : R.color.colorTextPrimary);
        holder.textCategory.setBackgroundResource(bgRes);
        holder.textCategory.setTextColor(textColor);
        holder.textCategory.setTypeface(null, isSelected ? Typeface.BOLD : Typeface.NORMAL);

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);
            if (listener != null) listener.onCategoryClick(category, position);
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView textCategory;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            textCategory = itemView.findViewById(R.id.textCategory);
        }
    }

    public void setSelectedPosition(int pos) {
        int prev = selectedPosition;
        selectedPosition = pos;
        notifyItemChanged(prev);
        notifyItemChanged(selectedPosition);
    }
}