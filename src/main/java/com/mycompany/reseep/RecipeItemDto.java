package com.mycompany.reseep;

public class RecipeItemDto {
    private Long ingredientId;
    private double qty;

    public RecipeItemDto() {}
    public RecipeItemDto(Long ingredientId, double qty) {
        this.ingredientId = ingredientId;
        this.qty = qty;
    }
    public Long getIngredientId() { return ingredientId; }
    public void setIngredientId(Long ingredientId) { this.ingredientId = ingredientId; }
    public double getQty() { return qty; }
    public void setQty(double qty) { this.qty = qty; }
}
