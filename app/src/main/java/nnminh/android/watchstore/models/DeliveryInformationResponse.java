package nnminh.android.watchstore.models;

public class DeliveryInformationResponse extends BaseResponse {
    private DeliveryInformation deliveryAddress;

    public DeliveryInformation getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(DeliveryInformation deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
