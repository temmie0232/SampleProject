package com.example.simplelibrary.book;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {

    @EntityGraph(attributePaths = {"authors", "categories"})
    Optional<Book> findWithAuthorsAndCategoriesById(String id);

    @EntityGraph(attributePaths = {"authors", "categories"})
    Page<Book> findAll(Specification<Book> spec, Pageable pageable);
}
