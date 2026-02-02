package com.example.simplelibrary.loan.dto;

import com.example.simplelibrary.loan.LoanStatus;
import java.time.Instant;
import java.time.LocalDate;

public class LoanResponse {
    private String id;
    private String copyId;
    private String bookId;
    private String bookTitle;
    private LoanStatus status;
    private Instant borrowedAt;
    private LocalDate dueDate;
    private Instant returnedAt;

    public LoanResponse(String id, String copyId, String bookId, String bookTitle,
                        LoanStatus status, Instant borrowedAt, LocalDate dueDate, Instant returnedAt) {
        this.id = id;
        this.copyId = copyId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.status = status;
        this.borrowedAt = borrowedAt;
        this.dueDate = dueDate;
        this.returnedAt = returnedAt;
    }

    public String getId() {
        return id;
    }

    public String getCopyId() {
        return copyId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public Instant getBorrowedAt() {
        return borrowedAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Instant getReturnedAt() {
        return returnedAt;
    }
}

