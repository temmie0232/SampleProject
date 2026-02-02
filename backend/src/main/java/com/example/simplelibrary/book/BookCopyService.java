package com.example.simplelibrary.book;

import com.example.simplelibrary.book.dto.BookCopyResponse;
import com.example.simplelibrary.book.dto.BookCopyUpdateRequest;
import com.example.simplelibrary.book.dto.CreateCopiesRequest;
import com.example.simplelibrary.exception.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookCopyService {
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;

    public BookCopyService(BookRepository bookRepository, BookCopyRepository bookCopyRepository) {
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
    }

    @Transactional(readOnly = true)
    public List<BookCopyResponse> listByBook(String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        return bookCopyRepository.findByBookId(book.getId()).stream()
                .map(copy -> new BookCopyResponse(copy.getId(), bookId, copy.getStatus()))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<BookCopyResponse> addCopies(String bookId, CreateCopiesRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        int count = request.getCount();
        List<BookCopy> copies = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            BookCopy copy = new BookCopy();
            copy.setBook(book);
            copy.setStatus(BookCopyStatus.AVAILABLE);
            copies.add(copy);
        }
        List<BookCopy> saved = bookCopyRepository.saveAll(copies);
        return saved.stream()
                .map(copy -> new BookCopyResponse(copy.getId(), bookId, copy.getStatus()))
                .collect(Collectors.toList());
    }

    @Transactional
    public BookCopyResponse updateStatus(String copyId, BookCopyUpdateRequest request) {
        BookCopy copy = bookCopyRepository.findById(copyId)
                .orElseThrow(() -> new NotFoundException("Copy not found"));
        copy.setStatus(request.getStatus());
        return new BookCopyResponse(copy.getId(), copy.getBook().getId(), copy.getStatus());
    }
}
