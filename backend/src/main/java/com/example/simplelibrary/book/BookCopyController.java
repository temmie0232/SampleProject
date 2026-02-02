package com.example.simplelibrary.book;

import com.example.simplelibrary.book.dto.BookCopyResponse;
import com.example.simplelibrary.book.dto.BookCopyUpdateRequest;
import com.example.simplelibrary.book.dto.CreateCopiesRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class BookCopyController {
    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService) {
        this.bookCopyService = bookCopyService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/books/{bookId}/copies")
    public List<BookCopyResponse> listByBook(@PathVariable String bookId) {
        return bookCopyService.listByBook(bookId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/books/{bookId}/copies")
    public List<BookCopyResponse> addCopies(@PathVariable String bookId, @Valid @RequestBody CreateCopiesRequest request) {
        return bookCopyService.addCopies(bookId, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/copies/{copyId}")
    public BookCopyResponse updateStatus(@PathVariable String copyId, @Valid @RequestBody BookCopyUpdateRequest request) {
        return bookCopyService.updateStatus(copyId, request);
    }
}

