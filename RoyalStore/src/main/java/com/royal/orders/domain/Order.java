package com.royal.orders.domain;

import com.royal.products.domain.ElectronicProduct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private Instant issuedAt;
    private Instant lastModifiedAt;
    private ElectronicProduct purchasedProduct;
    private OrderStatus status = OrderStatus.Pending;
    private int quantity;
    private float price;

    @Contract(pure = true)
    public Order(@NotNull Order other) {
        this.id = other.id;
        this.issuedAt = other.issuedAt;
        this.lastModifiedAt = other.lastModifiedAt;
        this.purchasedProduct = other.purchasedProduct;
        this.status = other.status;
        this.quantity = other.quantity;
        this.price = other.price;
    }

    public void setIssuedAt(Instant now) {
        this.issuedAt = now;
    }

    public void setIssuedAt(String iso8601DateFormatString) {
        this.issuedAt = Instant.parse(iso8601DateFormatString);
    }

    public Order cancel() {
        var cancelledOrder = new Order(this);
        if (cancelledOrder.status == OrderStatus.Pending) cancelledOrder.status = OrderStatus.Cancelled;
        cancelledOrder.lastModifiedAt = Instant.now();
        return cancelledOrder;
    }

    public Order progress() {
        var progressedOrder = new Order(this);
        if (this.status == OrderStatus.Pending) progressedOrder.status = OrderStatus.Confirmed;
        progressedOrder.lastModifiedAt = Instant.now();
        return progressedOrder;
    }

}
