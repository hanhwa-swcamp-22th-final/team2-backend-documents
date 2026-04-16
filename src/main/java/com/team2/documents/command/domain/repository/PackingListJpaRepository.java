package com.team2.documents.command.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team2.documents.command.domain.entity.PackingList;

public interface PackingListJpaRepository extends JpaRepository<PackingList, Long> {

    Optional<PackingList> findByPlCode(String plCode);

    Optional<PackingList> findByPoId(Long poId);

    long countByPoId(Long poId);
}
