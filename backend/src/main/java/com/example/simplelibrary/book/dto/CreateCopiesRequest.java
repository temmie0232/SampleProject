package com.example.simplelibrary.book.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class CreateCopiesRequest {
    @Min(1)
    @Max(50)
    private int count = 1;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

