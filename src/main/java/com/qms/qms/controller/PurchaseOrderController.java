package com.qms.qms.controller;

import com.qms.qms.dto.po.PurchaseOrderResponse;
import com.qms.qms.repository.PurchaseOrderRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public PurchaseOrderController(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @GetMapping
    public List<PurchaseOrderResponse> search(@RequestParam(required = false) String search) {
        var orders = (search == null || search.isBlank())
                ? purchaseOrderRepository.findAll()
                : purchaseOrderRepository.findByPoCodeContainingIgnoreCase(search);
        return orders.stream().map(PurchaseOrderResponse::from).toList();
    }
}
