package com.example.simplelibrary.book;

import com.example.simplelibrary.exception.NotFoundException;
import com.example.simplelibrary.storage.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class BookCoverService {
    private final BookRepository bookRepository;
    private final FileStorageService fileStorageService;

    public BookCoverService(BookRepository bookRepository, FileStorageService fileStorageService) {
        this.bookRepository = bookRepository;
        this.fileStorageService = fileStorageService;
    }

    public Resource getCover(String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        if (book.getCoverPath() == null) {
            throw new NotFoundException("Cover not found");
        }
        return fileStorageService.loadAsResource(book.getCoverPath());
    }
}

