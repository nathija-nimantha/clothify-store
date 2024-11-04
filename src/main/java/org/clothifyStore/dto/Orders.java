package org.clothifyStore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Orders {
    private String id;
    private String customerId;
    private String orderDate;
    private String status;
    private double totalAmount;
    private String shippingAddress;
    private String paymentMethod;
    private String trackingNumber;
}
