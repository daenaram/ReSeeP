package com.mycompany.reseep;

public class IngredientDto {
    private Long id;
    private String name;
    private String unit;
    private double qtyOnHand;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public double getQtyOnHand() { return qtyOnHand; }
    public void setQtyOnHand(double qtyOnHand) { this.qtyOnHand = qtyOnHand; }
}