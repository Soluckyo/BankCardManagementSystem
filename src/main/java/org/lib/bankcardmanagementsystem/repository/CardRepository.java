package org.lib.bankcardmanagementsystem.repository;

import org.lib.bankcardmanagementsystem.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    Page<Card> findAllByOwnerUser_IdUser(Long ownerUserIdUser, Pageable pageable);

    Page<Card> findByBlockRequest(Boolean blockRequest, Pageable pageable);
}
