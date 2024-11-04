package org.clothifyStore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    private int id;
    private String name;
    private String brand;
    private String color;
    private String material;
    private String size;
    private double price;
    private int quantity;
}
