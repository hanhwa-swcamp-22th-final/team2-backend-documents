package com.team2.documents.command.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.documents.command.domain.entity.PackingList;

public interface PackingListJpaRepository extends JpaRepository<PackingList, String> {
}
