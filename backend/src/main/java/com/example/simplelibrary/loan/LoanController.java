package com.example.simplelibrary.loan;

import com.example.simplelibrary.loan.dto.LoanResponse;
import com.example.simplelibrary.security.SecurityUtils;
import com.example.simplelibrary.security.UserPrincipal;
import com.example.simplelibrary.web.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/copies/{copyId}/borrow")
    public LoanResponse borrow(@PathVariable String copyId) {
        UserPrincipal principal = SecurityUtils.currentUser();
        return loanService.borrow(copyId, principal.getId());
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/books/{bookId}/borrow")
    public LoanResponse borrowByBook(@PathVariable String bookId) {
        UserPrincipal principal = SecurityUtils.currentUser();
        return loanService.borrowByBook(bookId, principal.getId());
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/loans/{loanId}/return")
    public LoanResponse returnBook(@PathVariable String loanId) {
        UserPrincipal principal = SecurityUtils.currentUser();
        return loanService.returnBook(loanId, principal.getId());
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/loans/me")
    public PageResponse<LoanResponse> myLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "borrowedAt,desc") String sort
    ) {
        Pageable pageable = createPageable(page, size, sort);
        UserPrincipal principal = SecurityUtils.currentUser();
        return loanService.listMyLoans(principal.getId(), pageable);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/loans/me/history")
    public PageResponse<LoanResponse> myHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "borrowedAt,desc") String sort
    ) {
        Pageable pageable = createPageable(page, size, sort);
        UserPrincipal principal = SecurityUtils.currentUser();
        return loanService.listMyHistory(principal.getId(), pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/loans")
    public PageResponse<LoanResponse> allLoans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "borrowedAt,desc") String sort
    ) {
        Pageable pageable = createPageable(page, size, sort);
        return loanService.listAll(pageable);
    }

    private Pageable createPageable(int page, int size, String sort) {
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, sortParts[0]));
    }
}
