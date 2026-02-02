package com.example.simplelibrary.book.dto;

import com.example.simplelibrary.book.BookCopyStatus;

public class BookCopyResponse {
    private String id;
    private String bookId;
    private BookCopyStatus status;

    public BookCopyResponse(String id, String bookId, BookCopyStatus status) {
        this.id = id;
        this.bookId = bookId;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getBookId() {
        return bookId;
    }

    public BookCopyStatus getStatus() {
        return status;
    }
}

