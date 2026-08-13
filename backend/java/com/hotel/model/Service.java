package java.com.hotel.model;

// File: src/main/java/model/Service.java

import java.math.BigDecimal;

/**
 * Service entity representing hotel services (food, laundry, spa, etc.)
 */
public class Service {
    private int id;
    private String name;
    private String category;           // SERVICE, MINIBAR, DAMAGE_FEE
    private BigDecimal price;
    private String unit;               // Kg, Bottle, Item, etc.
    private String description;

    public Service() {}

    public Service(int id, String name, String category, BigDecimal price, String unit, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.unit = unit;
        this.description = description;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}