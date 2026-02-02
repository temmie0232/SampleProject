package com.example.simplelibrary.loan;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, String> {
    @EntityGraph(attributePaths = {"copy", "copy.book"})
    Page<Loan> findByBorrowerId(String borrowerId, Pageable pageable);

    @EntityGraph(attributePaths = {"copy", "copy.book"})
    Page<Loan> findByBorrowerIdAndStatus(String borrowerId, LoanStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"copy", "copy.book"})
    Page<Loan> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"copy", "copy.book"})
    Optional<Loan> findWithCopyById(String id);
}

