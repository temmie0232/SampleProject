package com.example.simplelibrary.author.dto;

public class AuthorResponse {
    private String id;
    private String name;

    public AuthorResponse(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

