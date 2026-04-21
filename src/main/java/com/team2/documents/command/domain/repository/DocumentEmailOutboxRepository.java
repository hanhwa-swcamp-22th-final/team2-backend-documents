package com.team2.documents.command.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team2.documents.command.domain.entity.DocumentEmailOutbox;
import com.team2.documents.command.domain.entity.enums.DocumentEmailOutboxStatus;

import jakarta.persistence.LockModeType;

public interface DocumentEmailOutboxRepository extends JpaRepository<DocumentEmailOutbox, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from DocumentEmailOutbox o where o.id = :id")
    Optional<DocumentEmailOutbox> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select o.id
            from DocumentEmailOutbox o
            where o.status = :status
              and o.attempts < o.maxAttempts
              and o.nextAttemptAt <= :now
            order by o.createdAt asc
            """)
    List<Long> findReadyIds(@Param("status") DocumentEmailOutboxStatus status,
                            @Param("now") LocalDateTime now,
                            Pageable pageable);
}
