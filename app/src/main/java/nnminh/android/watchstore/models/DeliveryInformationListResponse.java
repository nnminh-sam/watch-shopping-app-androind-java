package nnminh.android.watchstore.models;

import java.util.List;

public class DeliveryInformationListResponse extends BaseResponse {
    private List<DeliveryInformation> delivery_addresses;

    public List<DeliveryInformation> getDeliveryAddresses() {
        return delivery_addresses;
    }

    public void setDeliveryAddresses(List<DeliveryInformation> delivery_addresses) {
        this.delivery_addresses = delivery_addresses;
    }
}
