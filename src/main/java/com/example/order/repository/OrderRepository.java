package com.example.order.repository;

import com.example.order.domain.Order;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("""
     select distinct o from Order o
            left join fetch o.lines l
            left join fetch l.product
            where o.id = :id
    """)
    Optional<Order> findByIdWithLines(@Param("id") UUID id);

    @Query("""
    select o.id, o.status, o.createdAt, count(l)
    from Order o
    left join o.lines l
    group by o.id, o.status, o.createdAt
    order by o.createdAt desc
""")
    List<Object[]> findOrderSummariesRaw();

    Optional<Order> findByIdempotencyKey(String idempotencyKey);
}
