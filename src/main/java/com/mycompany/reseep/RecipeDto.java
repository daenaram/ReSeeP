package com.mycompany.reseep;

import java.util.ArrayList;
import java.util.List;

public class RecipeDto {
    private Long id;
    private String name;
    private int servings;
    private String notes;
    private List<RecipeItemDto> items = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getServings() { return servings; }
    public void setServings(int servings) { this.servings = servings; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<RecipeItemDto> getItems() { return items; }
    public void setItems(List<RecipeItemDto> items) { this.items = items; }
}
