package com.example.simplelibrary.loan.dto;

import com.example.simplelibrary.loan.LoanStatus;
import java.time.Instant;
import java.time.LocalDate;

public class LoanResponse {
    private String id;
    private String copyId;
    private String bookId;
    private String bookTitle;
    private String borrowerId;
    private String borrowerEmail;
    private String borrowerDisplayName;
    private LoanStatus status;
    private Instant borrowedAt;
    private LocalDate dueDate;
    private Instant returnedAt;

    public LoanResponse(String id, String copyId, String bookId, String bookTitle,
                        String borrowerId, String borrowerEmail, String borrowerDisplayName,
                        LoanStatus status, Instant borrowedAt, LocalDate dueDate, Instant returnedAt) {
        this.id = id;
        this.copyId = copyId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.borrowerId = borrowerId;
        this.borrowerEmail = borrowerEmail;
        this.borrowerDisplayName = borrowerDisplayName;
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

    public String getBorrowerId() {
        return borrowerId;
    }

    public String getBorrowerEmail() {
        return borrowerEmail;
    }

    public String getBorrowerDisplayName() {
        return borrowerDisplayName;
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
