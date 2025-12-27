package parfumerie.parfilya.models.mysql;

public enum OrderStatus {
    PENDING,       // En attente de confirmation
    CONFIRMED,     // Confirmée
    PROCESSING,    // En cours de préparation
    SHIPPED,       // Expédiée
    DELIVERED,     // Livrée
    CANCELLED      // Annulée
}