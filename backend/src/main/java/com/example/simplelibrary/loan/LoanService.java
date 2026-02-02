package com.example.simplelibrary.loan;

import com.example.simplelibrary.book.BookCopy;
import com.example.simplelibrary.book.BookCopyRepository;
import com.example.simplelibrary.book.BookCopyStatus;
import com.example.simplelibrary.exception.ConflictException;
import com.example.simplelibrary.exception.ForbiddenException;
import com.example.simplelibrary.exception.NotFoundException;
import com.example.simplelibrary.loan.dto.LoanResponse;
import com.example.simplelibrary.user.User;
import com.example.simplelibrary.user.UserRepository;
import com.example.simplelibrary.web.PageResponse;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanService {
    private static final int DEFAULT_LOAN_DAYS = 14;
    private final BookCopyRepository bookCopyRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    public LoanService(BookCopyRepository bookCopyRepository, LoanRepository loanRepository, UserRepository userRepository) {
        this.bookCopyRepository = bookCopyRepository;
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public LoanResponse borrow(String copyId, String borrowerId) {
        BookCopy copy = bookCopyRepository.findByIdForUpdate(copyId)
                .orElseThrow(() -> new NotFoundException("Copy not found"));
        if (copy.getStatus() != BookCopyStatus.AVAILABLE) {
            throw new ConflictException("Copy is not available");
        }
        User borrower = userRepository.findById(borrowerId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        copy.setStatus(BookCopyStatus.LOANED);
        Loan loan = new Loan();
        loan.setCopy(copy);
        loan.setBorrower(borrower);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setBorrowedAt(Instant.now());
        loan.setDueDate(LocalDate.now().plusDays(DEFAULT_LOAN_DAYS));
        loan.setRenewCount(0);
        Loan saved = loanRepository.save(loan);
        return toResponse(saved);
    }

    @Transactional
    public LoanResponse borrowByBook(String bookId, String borrowerId) {
        BookCopy copy = bookCopyRepository.findFirstByBookIdAndStatus(bookId, BookCopyStatus.AVAILABLE)
                .orElseThrow(() -> new ConflictException("No available copies"));
        User borrower = userRepository.findById(borrowerId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        copy.setStatus(BookCopyStatus.LOANED);
        Loan loan = new Loan();
        loan.setCopy(copy);
        loan.setBorrower(borrower);
        loan.setStatus(LoanStatus.ACTIVE);
        loan.setBorrowedAt(Instant.now());
        loan.setDueDate(LocalDate.now().plusDays(DEFAULT_LOAN_DAYS));
        loan.setRenewCount(0);
        Loan saved = loanRepository.save(loan);
        return toResponse(saved);
    }

    @Transactional
    public LoanResponse returnBook(String loanId, String borrowerId) {
        Loan loan = loanRepository.findWithCopyById(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found"));
        if (!loan.getBorrower().getId().equals(borrowerId)) {
            throw new ForbiddenException("Cannot return other user's loan");
        }
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new ConflictException("Loan is already returned");
        }
        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnedAt(Instant.now());
        loan.getCopy().setStatus(BookCopyStatus.AVAILABLE);
        return toResponse(loan);
    }

    @Transactional(readOnly = true)
    public PageResponse<LoanResponse> listMyLoans(String borrowerId, Pageable pageable) {
        Page<Loan> page = loanRepository.findByBorrowerIdAndStatus(borrowerId, LoanStatus.ACTIVE, pageable);
        return pageToResponse(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<LoanResponse> listMyHistory(String borrowerId, Pageable pageable) {
        Page<Loan> page = loanRepository.findByBorrowerId(borrowerId, pageable);
        return pageToResponse(page);
    }

    @Transactional(readOnly = true)
    public PageResponse<LoanResponse> listAll(Pageable pageable) {
        Page<Loan> page = loanRepository.findAll(pageable);
        return pageToResponse(page);
    }

    private PageResponse<LoanResponse> pageToResponse(Page<Loan> page) {
        List<LoanResponse> items = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return new PageResponse<>(items, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    private LoanResponse toResponse(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getCopy().getId(),
                loan.getCopy().getBook().getId(),
                loan.getCopy().getBook().getTitle(),
                loan.getStatus(),
                loan.getBorrowedAt(),
                loan.getDueDate(),
                loan.getReturnedAt()
        );
    }
}
