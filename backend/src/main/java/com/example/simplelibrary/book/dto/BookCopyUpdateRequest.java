package com.example.simplelibrary.book.dto;

import com.example.simplelibrary.book.BookCopyStatus;
import jakarta.validation.constraints.NotNull;

public class BookCopyUpdateRequest {
    @NotNull
    private BookCopyStatus status;

    public BookCopyStatus getStatus() {
        return status;
    }

    public void setStatus(BookCopyStatus status) {
        this.status = status;
    }
}

