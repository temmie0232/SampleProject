package com.example.simplelibrary.book.dto;

import com.example.simplelibrary.author.dto.AuthorResponse;
import com.example.simplelibrary.category.dto.CategoryResponse;
import java.util.List;

public class BookResponse {
    private String id;
    private String title;
    private String description;
    private String coverUrl;
    private List<AuthorResponse> authors;
    private List<CategoryResponse> categories;
    private long totalCopies;
    private long availableCopies;

    public BookResponse(String id, String title, String description, String coverUrl,
                        List<AuthorResponse> authors, List<CategoryResponse> categories,
                        long totalCopies, long availableCopies) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.coverUrl = coverUrl;
        this.authors = authors;
        this.categories = categories;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public List<AuthorResponse> getAuthors() {
        return authors;
    }

    public List<CategoryResponse> getCategories() {
        return categories;
    }

    public long getTotalCopies() {
        return totalCopies;
    }

    public long getAvailableCopies() {
        return availableCopies;
    }
}

