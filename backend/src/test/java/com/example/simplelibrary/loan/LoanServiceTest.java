package com.example.simplelibrary.loan;

import com.example.simplelibrary.AbstractIntegrationTest;
import com.example.simplelibrary.book.Book;
import com.example.simplelibrary.book.BookCopy;
import com.example.simplelibrary.book.BookCopyRepository;
import com.example.simplelibrary.book.BookCopyStatus;
import com.example.simplelibrary.book.BookRepository;
import com.example.simplelibrary.exception.ConflictException;
import com.example.simplelibrary.loan.dto.LoanResponse;
import com.example.simplelibrary.user.Role;
import com.example.simplelibrary.user.User;
import com.example.simplelibrary.user.UserRepository;
import com.example.simplelibrary.web.PageResponse;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoanServiceTest extends AbstractIntegrationTest {

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoanRepository loanRepository;

    @Test
    void borrowAndReturnFlow() {
        User borrower = createUser("borrower@example.com", "Borrower");
        Book book = createBook("Sample Book");
        BookCopy copy = createCopy(book);

        LoanResponse loan = loanService.borrow(copy.getId(), borrower.getId());
        assertThat(loan.getStatus()).isEqualTo(LoanStatus.ACTIVE);

        BookCopy updatedCopy = bookCopyRepository.findById(copy.getId()).orElseThrow();
        assertThat(updatedCopy.getStatus()).isEqualTo(BookCopyStatus.LOANED);

        LoanResponse returned = loanService.returnBook(loan.getId(), borrower.getId());
        assertThat(returned.getStatus()).isEqualTo(LoanStatus.RETURNED);

        BookCopy returnedCopy = bookCopyRepository.findById(copy.getId()).orElseThrow();
        assertThat(returnedCopy.getStatus()).isEqualTo(BookCopyStatus.AVAILABLE);
    }

    @Test
    void borrowRejectsWhenAlreadyLoaned() {
        User borrower = createUser("borrower2@example.com", "Borrower2");
        Book book = createBook("Locked Book");
        BookCopy copy = createCopy(book);

        loanService.borrow(copy.getId(), borrower.getId());
        assertThrows(ConflictException.class, () -> loanService.borrow(copy.getId(), borrower.getId()));
    }

    @Test
    void adminFilterByEmailAndDate() {
        User alice = createUser("alice@example.com", "Alice");
        User bob = createUser("bob@example.com", "Bob");
        Book bookA = createBook("Book A");
        Book bookB = createBook("Book B");
        BookCopy copyA = createCopy(bookA);
        BookCopy copyB = createCopy(bookB);

        LoanResponse loanA = loanService.borrow(copyA.getId(), alice.getId());
        LoanResponse loanB = loanService.borrow(copyB.getId(), bob.getId());

        Loan entityA = loanRepository.findById(loanA.getId()).orElseThrow();
        Loan entityB = loanRepository.findById(loanB.getId()).orElseThrow();
        entityA.setBorrowedAt(Instant.parse("2026-01-10T00:00:00Z"));
        entityB.setBorrowedAt(Instant.parse("2026-02-01T00:00:00Z"));
        loanRepository.saveAll(List.of(entityA, entityB));

        PageResponse<LoanResponse> result = loanService.listAll(
                "ACTIVE",
                null,
                "alice",
                null,
                "Book A",
                "2026-01-01",
                "2026-01-31",
                PageRequest.of(0, 20)
        );

        assertThat(result.getTotalItems()).isEqualTo(1);
        assertThat(result.getItems().get(0).getBorrowerEmail()).contains("alice");
    }

    private User createUser(String email, String name) {
        User user = new User();
        user.setEmail(email);
        user.setDisplayName(name);
        user.setRole(Role.MEMBER);
        user.setPasswordHash(passwordEncoder.encode("Passw0rd!"));
        return userRepository.save(user);
    }

    private Book createBook(String title) {
        Book book = new Book();
        book.setTitle(title);
        book.setDescription("desc");
        return bookRepository.save(book);
    }

    private BookCopy createCopy(Book book) {
        BookCopy copy = new BookCopy();
        copy.setBook(book);
        copy.setStatus(BookCopyStatus.AVAILABLE);
        return bookCopyRepository.save(copy);
    }
}
