package parfumerie.parfilya.dto.order;

public class CreateOrderRequest {
    private Long addressId;

    public CreateOrderRequest() {}

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }
}
