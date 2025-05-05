package org.lib.bankcardmanagementsystem.repository;

import org.lib.bankcardmanagementsystem.entity.Card;
import org.lib.bankcardmanagementsystem.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long>, JpaSpecificationExecutor<Card> {
    Page<Card> findAllByOwnerUser_IdUser(Long ownerUserIdUser, Pageable pageable);

    Page<Card> findByBlockRequest(Boolean blockRequest, Pageable pageable);

    Page<Card> findAllByOwnerUser_IdUserAndStatusAndBalanceGreaterThanEqual(Long idUser, Status status, BigDecimal minBalance, Pageable pageable);

    Page<Card> findAllByOwnerUser_IdUserAndStatus(Long idUser, Status status, Pageable pageable);

    Page<Card> findAllByOwnerUser_IdUserAndBalanceGreaterThanEqual(Long idUser, BigDecimal minBalance, Pageable pageable);
}
