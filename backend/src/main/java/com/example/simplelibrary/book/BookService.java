package com.example.simplelibrary.book;

import com.example.simplelibrary.author.Author;
import com.example.simplelibrary.author.AuthorRepository;
import com.example.simplelibrary.book.dto.BookRequest;
import com.example.simplelibrary.book.dto.BookResponse;
import com.example.simplelibrary.category.Category;
import com.example.simplelibrary.category.CategoryRepository;
import com.example.simplelibrary.category.dto.CategoryResponse;
import com.example.simplelibrary.author.dto.AuthorResponse;
import com.example.simplelibrary.exception.ConflictException;
import com.example.simplelibrary.exception.NotFoundException;
import com.example.simplelibrary.storage.FileStorageService;
import com.example.simplelibrary.web.PageResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookCopyRepository bookCopyRepository;
    private final FileStorageService fileStorageService;

    public BookService(BookRepository bookRepository,
                       AuthorRepository authorRepository,
                       CategoryRepository categoryRepository,
                       BookCopyRepository bookCopyRepository,
                       FileStorageService fileStorageService) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional(readOnly = true)
    public PageResponse<BookResponse> list(String keyword, String authorId, String categoryId, Pageable pageable) {
        Specification<Book> spec = Specification.where(BookSpecifications.titleOrDescriptionContains(keyword))
                .and(BookSpecifications.hasAuthor(authorId))
                .and(BookSpecifications.hasCategory(categoryId));
        Page<Book> page = bookRepository.findAll(spec, pageable);
        List<Book> books = page.getContent();
        Map<String, Long> totalMap = countTotals(books);
        Map<String, Long> availableMap = countAvailable(books);
        List<BookResponse> items = books.stream()
                .map(book -> toResponse(book, totalMap.getOrDefault(book.getId(), 0L),
                        availableMap.getOrDefault(book.getId(), 0L)))
                .collect(Collectors.toList());
        return new PageResponse<>(items, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Transactional(readOnly = true)
    public BookResponse get(String id) {
        Book book = bookRepository.findWithAuthorsAndCategoriesById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        long total = bookCopyRepository.countByBookId(id);
        long available = bookCopyRepository.countByBookIdAndStatus(id, BookCopyStatus.AVAILABLE);
        return toResponse(book, total, available);
    }

    @Transactional
    public BookResponse create(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setDescription(request.getDescription());
        book.setAuthors(resolveAuthors(request.getAuthorIds()));
        book.setCategories(resolveCategories(request.getCategoryIds()));
        Book saved = bookRepository.save(book);
        return toResponse(saved, 0L, 0L);
    }

    @Transactional
    public BookResponse update(String id, BookRequest request) {
        Book book = bookRepository.findWithAuthorsAndCategoriesById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        book.setTitle(request.getTitle());
        book.setDescription(request.getDescription());
        if (request.getAuthorIds() != null) {
            book.setAuthors(resolveAuthors(request.getAuthorIds()));
        }
        if (request.getCategoryIds() != null) {
            book.setCategories(resolveCategories(request.getCategoryIds()));
        }
        return toResponse(book, bookCopyRepository.countByBookId(id),
                bookCopyRepository.countByBookIdAndStatus(id, BookCopyStatus.AVAILABLE));
    }

    @Transactional
    public void delete(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        if (bookCopyRepository.existsByBookId(id)) {
            throw new ConflictException("Cannot delete book with copies");
        }
        bookRepository.delete(book);
    }

    @Transactional
    public BookResponse uploadCover(String id, MultipartFile file) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        String path = fileStorageService.saveBookCover(id, file);
        book.setCoverPath(path);
        return get(id);
    }

    @Transactional
    public BookResponse deleteCover(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        if (book.getCoverPath() == null) {
            throw new NotFoundException("Cover not found");
        }
        fileStorageService.delete(book.getCoverPath());
        book.setCoverPath(null);
        return get(id);
    }

    private Set<Author> resolveAuthors(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new java.util.HashSet<>();
        }
        List<Author> authors = authorRepository.findAllById(ids);
        if (authors.size() != ids.size()) {
            throw new NotFoundException("Author not found");
        }
        return new java.util.HashSet<>(authors);
    }

    private Set<Category> resolveCategories(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return new java.util.HashSet<>();
        }
        List<Category> categories = categoryRepository.findAllById(ids);
        if (categories.size() != ids.size()) {
            throw new NotFoundException("Category not found");
        }
        return new java.util.HashSet<>(categories);
    }

    private BookResponse toResponse(Book book, long total, long available) {
        String coverUrl = book.getCoverPath() == null ? null : "/api/v1/books/" + book.getId() + "/cover";
        List<AuthorResponse> authors = book.getAuthors().stream()
                .map(author -> new AuthorResponse(author.getId(), author.getName()))
                .collect(Collectors.toList());
        List<CategoryResponse> categories = book.getCategories().stream()
                .map(category -> new CategoryResponse(category.getId(), category.getName(), category.isActive()))
                .collect(Collectors.toList());
        return new BookResponse(book.getId(), book.getTitle(), book.getDescription(), coverUrl, authors, categories, total, available);
    }

    private Map<String, Long> countTotals(List<Book> books) {
        Map<String, Long> result = new HashMap<>();
        List<String> bookIds = books.stream().map(Book::getId).toList();
        if (bookIds.isEmpty()) {
            return result;
        }
        for (BookCopyRepository.BookCount row : bookCopyRepository.countTotalByBookIds(bookIds)) {
            result.put(row.getBookId(), row.getTotal());
        }
        return result;
    }

    private Map<String, Long> countAvailable(List<Book> books) {
        Map<String, Long> result = new HashMap<>();
        List<String> bookIds = books.stream().map(Book::getId).toList();
        if (bookIds.isEmpty()) {
            return result;
        }
        for (BookCopyRepository.BookCount row : bookCopyRepository.countByBookIdsAndStatus(bookIds, BookCopyStatus.AVAILABLE)) {
            result.put(row.getBookId(), row.getTotal());
        }
        return result;
    }
}
