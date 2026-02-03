package com.example.simplelibrary.loan;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

public interface LoanRepository extends JpaRepository<Loan, String>, JpaSpecificationExecutor<Loan> {
    @EntityGraph(attributePaths = {"copy", "copy.book", "borrower"})
    Page<Loan> findByBorrowerId(String borrowerId, Pageable pageable);

    @EntityGraph(attributePaths = {"copy", "copy.book", "borrower"})
    Page<Loan> findByBorrowerIdAndStatus(String borrowerId, LoanStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"copy", "copy.book", "borrower"})
    Page<Loan> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"copy", "copy.book", "borrower"})
    Optional<Loan> findWithCopyById(String id);

    @EntityGraph(attributePaths = {"copy", "copy.book", "borrower"})
    Page<Loan> findAll(Specification<Loan> spec, Pageable pageable);
}
