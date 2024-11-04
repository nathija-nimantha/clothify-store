package org.clothifyStore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.clothifyStore.dto.Customer;

import java.sql.Timestamp;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Timestamp orderDate;
    private String status;
    private double totalAmount;

    @Column(length = 1000)
    private String shippingAddress;

    private String paymentMethod;
    private String trackingNumber;
}
