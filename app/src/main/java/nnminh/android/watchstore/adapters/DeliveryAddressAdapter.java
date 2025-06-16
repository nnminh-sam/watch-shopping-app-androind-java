package nnminh.android.watchstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nnminh.android.watchstore.R;
import nnminh.android.watchstore.models.DeliveryInformation;

public class DeliveryAddressAdapter extends RecyclerView.Adapter<DeliveryAddressAdapter.ViewHolder> {
    private List<DeliveryInformation> addresses;

    public DeliveryAddressAdapter(List<DeliveryInformation> addresses) {
        this.addresses = addresses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_delivery_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeliveryInformation address = addresses.get(position);
        holder.textName.setText(address.getFull_name());
        holder.textPhone.setText(address.getPhone_number());
        holder.textAddress.setText(String.format("%s, %s, %s, %s",
                address.getSpecific_address(),
                address.getStreet(),
                address.getDistrict(),
                address.getCity()));
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public void updateAddresses(List<DeliveryInformation> newAddresses) {
        this.addresses = newAddresses;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textPhone, textAddress;

        ViewHolder(View view) {
            super(view);
            textName = view.findViewById(R.id.textName);
            textPhone = view.findViewById(R.id.textPhone);
            textAddress = view.findViewById(R.id.textAddress);
        }
    }
} 