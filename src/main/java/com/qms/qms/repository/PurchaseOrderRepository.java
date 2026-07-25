package com.qms.qms.repository;

import com.qms.qms.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {

    @EntityGraph(attributePaths = {"style", "customer"})
    List<PurchaseOrder> findAll();

    @EntityGraph(attributePaths = {"style", "customer"})
    List<PurchaseOrder> findByPoCodeContainingIgnoreCase(String search);
}
