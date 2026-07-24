package com.qms.qms.repository;

import com.qms.qms.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByPoCodeContainingIgnoreCase(String search);
}
