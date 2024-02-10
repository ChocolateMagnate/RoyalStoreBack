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
public class ProductOrder {
    @Id
    private String id;
    private Instant issuedAt;
    private Instant lastModifiedAt;
    private ElectronicProduct purchasedProduct;
    private OrderStatus status;
    private int quantity;
    private float price;

    @Contract(pure = true)
    public ProductOrder(@NotNull ProductOrder other) {
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

    public ProductOrder cancel() {
        var cancelledOrder = new ProductOrder(this);
        if (cancelledOrder.status != OrderStatus.Cancelled) cancelledOrder.status = OrderStatus.Cancelled;
        cancelledOrder.lastModifiedAt = Instant.now();
        return cancelledOrder;
    }

    public ProductOrder progress() {
        var progressedOrder = new ProductOrder(this);
        if (this.status == OrderStatus.Pending) progressedOrder.status = OrderStatus.Confirmed;
        progressedOrder.lastModifiedAt = Instant.now();
        return progressedOrder;
    }

}
