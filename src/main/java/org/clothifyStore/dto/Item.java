package org.clothifyStore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Item {
    private int id;
    private String name;
    private String size;
    private String color;
    private String material;
    private String brand;
    private String style;
    private String occasion;
    private String careInstructions;
    private String fit;
    private double price;

    public Item(int id, String name, String brand, String color, String material, String size, double price) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.color = color;
        this.material = material;
        this.size = size;
        this.price = price;
    }
}
