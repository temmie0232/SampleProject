package com.example.simplelibrary.category.dto;

public class CategoryResponse {
    private String id;
    private String name;
    private boolean active;

    public CategoryResponse(String id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }
}

