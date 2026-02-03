package com.example.simplelibrary.loan;

import com.example.simplelibrary.book.Book;
import com.example.simplelibrary.book.BookCopy;
import com.example.simplelibrary.user.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.time.Instant;
import org.springframework.data.jpa.domain.Specification;

public class LoanSpecifications {
    public static Specification<Loan> hasStatus(LoanStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    public static Specification<Loan> hasBorrowerId(String borrowerId) {
        return (root, query, cb) -> {
            if (borrowerId == null || borrowerId.isBlank()) {
                return cb.conjunction();
            }
            Join<Loan, User> join = root.join("borrower", JoinType.LEFT);
            return cb.equal(join.get("id"), borrowerId);
        };
    }

    public static Specification<Loan> hasBorrowerEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) {
                return cb.conjunction();
            }
            Join<Loan, User> join = root.join("borrower", JoinType.LEFT);
            String like = "%" + email.toLowerCase() + "%";
            return cb.like(cb.lower(join.get("email")), like);
        };
    }

    public static Specification<Loan> hasBookId(String bookId) {
        return (root, query, cb) -> {
            if (bookId == null || bookId.isBlank()) {
                return cb.conjunction();
            }
            Join<Loan, BookCopy> copyJoin = root.join("copy", JoinType.LEFT);
            Join<BookCopy, Book> bookJoin = copyJoin.join("book", JoinType.LEFT);
            query.distinct(true);
            return cb.equal(bookJoin.get("id"), bookId);
        };
    }

    public static Specification<Loan> bookTitleContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            Join<Loan, BookCopy> copyJoin = root.join("copy", JoinType.LEFT);
            Join<BookCopy, Book> bookJoin = copyJoin.join("book", JoinType.LEFT);
            query.distinct(true);
            String like = "%" + keyword.toLowerCase() + "%";
            return cb.like(cb.lower(bookJoin.get("title")), like);
        };
    }

    public static Specification<Loan> borrowedAtFrom(Instant from) {
        return (root, query, cb) -> {
            if (from == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("borrowedAt"), from);
        };
    }

    public static Specification<Loan> borrowedAtTo(Instant toExclusive) {
        return (root, query, cb) -> {
            if (toExclusive == null) {
                return cb.conjunction();
            }
            return cb.lessThan(root.get("borrowedAt"), toExclusive);
        };
    }
}
