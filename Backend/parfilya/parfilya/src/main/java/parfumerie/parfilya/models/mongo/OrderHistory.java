package parfumerie.parfilya.models.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "order_history")
public class OrderHistory {

    @Id
    private String id;

    private Long mysqlOrderId;
    private Long userId;
    private String userEmail;
    private String status;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private List<OrderHistoryItem> items = new ArrayList<>();
    private List<StatusChange> statusHistory = new ArrayList<>();
    private ShippingInfo shippingInfo;

    public OrderHistory() {
        this.orderDate = LocalDateTime.now();
    }

    public OrderHistory(Long mysqlOrderId, Long userId) {
        this.mysqlOrderId = mysqlOrderId;
        this.userId = userId;
        this.orderDate = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getMysqlOrderId() {
        return mysqlOrderId;
    }

    public void setMysqlOrderId(Long mysqlOrderId) {
        this.mysqlOrderId = mysqlOrderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderHistoryItem> getItems() {
        return items;
    }

    public void setItems(List<OrderHistoryItem> items) {
        this.items = items;
    }

    public List<StatusChange> getStatusHistory() {
        return statusHistory;
    }

    public void setStatusHistory(List<StatusChange> statusHistory) {
        this.statusHistory = statusHistory;
    }

    public ShippingInfo getShippingInfo() {
        return shippingInfo;
    }

    public void setShippingInfo(ShippingInfo shippingInfo) {
        this.shippingInfo = shippingInfo;
    }

    public void addStatusChange(String newStatus, String comment) {
        this.statusHistory.add(new StatusChange(this.status, newStatus, comment));
        this.status = newStatus;
    }

    // Inner classes
    public static class OrderHistoryItem {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;

        public OrderHistoryItem() {}

        public OrderHistoryItem(Long productId, String productName, Integer quantity, BigDecimal unitPrice) {
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    }

    public static class StatusChange {
        private String fromStatus;
        private String toStatus;
        private String comment;
        private LocalDateTime changedAt;

        public StatusChange() {}

        public StatusChange(String fromStatus, String toStatus, String comment) {
            this.fromStatus = fromStatus;
            this.toStatus = toStatus;
            this.comment = comment;
            this.changedAt = LocalDateTime.now();
        }

        public String getFromStatus() { return fromStatus; }
        public void setFromStatus(String fromStatus) { this.fromStatus = fromStatus; }
        public String getToStatus() { return toStatus; }
        public void setToStatus(String toStatus) { this.toStatus = toStatus; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
        public LocalDateTime getChangedAt() { return changedAt; }
        public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
    }

    public static class ShippingInfo {
        private String address;
        private String city;
        private String postalCode;
        private String country;
        private String trackingNumber;

        public ShippingInfo() {}

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getTrackingNumber() { return trackingNumber; }
        public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    }
}
