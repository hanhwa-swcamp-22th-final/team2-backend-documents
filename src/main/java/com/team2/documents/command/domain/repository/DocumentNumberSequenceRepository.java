package com.team2.documents.command.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.team2.documents.command.domain.entity.DocumentNumberSequence;

import jakarta.persistence.LockModeType;

public interface DocumentNumberSequenceRepository extends JpaRepository<DocumentNumberSequence, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<DocumentNumberSequence> findByPrefix(String prefix);
}
