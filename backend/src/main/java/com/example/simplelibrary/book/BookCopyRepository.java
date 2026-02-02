package com.example.simplelibrary.book;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

public interface BookCopyRepository extends JpaRepository<BookCopy, String> {
    List<BookCopy> findByBookId(String bookId);
    long countByBookId(String bookId);
    long countByBookIdAndStatus(String bookId, BookCopyStatus status);
    boolean existsByBookId(String bookId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select bc from BookCopy bc where bc.id = :id")
    Optional<BookCopy> findByIdForUpdate(@Param("id") String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BookCopy> findFirstByBookIdAndStatus(String bookId, BookCopyStatus status);

    @Query("select bc.book.id as bookId, count(bc) as total from BookCopy bc where bc.book.id in :bookIds group by bc.book.id")
    List<BookCount> countTotalByBookIds(@Param("bookIds") List<String> bookIds);

    @Query("select bc.book.id as bookId, count(bc) as total from BookCopy bc where bc.book.id in :bookIds and bc.status = :status group by bc.book.id")
    List<BookCount> countByBookIdsAndStatus(@Param("bookIds") List<String> bookIds, @Param("status") BookCopyStatus status);

    interface BookCount {
        String getBookId();
        long getTotal();
    }
}
