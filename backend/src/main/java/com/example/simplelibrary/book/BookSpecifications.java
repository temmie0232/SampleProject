package com.example.simplelibrary.book;

import com.example.simplelibrary.author.Author;
import com.example.simplelibrary.category.Category;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecifications {
    public static Specification<Book> titleOrDescriptionContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }
            String like = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("description")), like)
            );
        };
    }

    public static Specification<Book> hasAuthor(String authorId) {
        return (root, query, cb) -> {
            if (authorId == null || authorId.isBlank()) {
                return cb.conjunction();
            }
            query.distinct(true);
            Join<Book, Author> join = root.join("authors", JoinType.LEFT);
            return cb.equal(join.get("id"), authorId);
        };
    }

    public static Specification<Book> hasCategory(String categoryId) {
        return (root, query, cb) -> {
            if (categoryId == null || categoryId.isBlank()) {
                return cb.conjunction();
            }
            query.distinct(true);
            Join<Book, Category> join = root.join("categories", JoinType.LEFT);
            return cb.equal(join.get("id"), categoryId);
        };
    }
}
