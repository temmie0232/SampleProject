package com.example.workflow.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {

    @Query("""
            select a from ApplicationEntity a
            where (:applicantUserId is null or a.applicantUserId = :applicantUserId)
              and (:status is null or a.status = :status)
              and (:q is null
                  or lower(a.currentAddress) like lower(concat('%', :q, '%'))
                  or lower(a.newAddress) like lower(concat('%', :q, '%'))
                  or lower(a.reason) like lower(concat('%', :q, '%')))
            """)
    Page<ApplicationEntity> search(
            @Param("applicantUserId") Long applicantUserId,
            @Param("status") ApplicationStatus status,
            @Param("q") String q,
            Pageable pageable
    );
}
