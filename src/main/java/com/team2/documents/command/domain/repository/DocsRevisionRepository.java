package com.team2.documents.command.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.documents.command.domain.entity.DocsRevision;

public interface DocsRevisionRepository extends JpaRepository<DocsRevision, Long> {

    List<DocsRevision> findAllByDocTypeAndDocIdOrderByCreatedAtAsc(String docType, Long docId);
}
