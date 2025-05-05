package org.lib.bankcardmanagementsystem.entity;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class CardSpecification {

    public static Specification<Card> hasOwnerId(Long ownerId) {
        return (root, query, cb) -> cb.equal(root.get("ownerUser").get("idUser"), ownerId);
    }

    public static Specification<Card> hasStatus(Status status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Card> hasMinBalance(BigDecimal minBalance) {
        return (root, query, cb) -> minBalance == null ? null : cb.greaterThanOrEqualTo(root.get("balance"), minBalance);
    }
}