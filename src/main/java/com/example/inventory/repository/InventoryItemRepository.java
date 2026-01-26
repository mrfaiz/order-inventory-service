package com.example.inventory.repository;

import com.example.inventory.domain.InventoryItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
    Optional<InventoryItem> findByProduct_Id(UUID productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select i from InventoryItem i
        join fetch i.product
        where i.product.id = :productId
    """)
    Optional<InventoryItem> findByProductIdForUpdate(@Param("productId") UUID productId);
}
