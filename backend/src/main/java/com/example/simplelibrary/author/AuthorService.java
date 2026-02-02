package com.example.simplelibrary.author;

import com.example.simplelibrary.author.dto.AuthorRequest;
import com.example.simplelibrary.author.dto.AuthorResponse;
import com.example.simplelibrary.exception.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Transactional(readOnly = true)
    public List<AuthorResponse> list() {
        return authorRepository.findAll().stream()
                .map(author -> new AuthorResponse(author.getId(), author.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public AuthorResponse create(AuthorRequest request) {
        Author author = new Author();
        author.setName(request.getName());
        Author saved = authorRepository.save(author);
        return new AuthorResponse(saved.getId(), saved.getName());
    }

    @Transactional
    public AuthorResponse update(String id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found"));
        author.setName(request.getName());
        return new AuthorResponse(author.getId(), author.getName());
    }

    @Transactional
    public void delete(String id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Author not found"));
        authorRepository.delete(author);
    }
}

