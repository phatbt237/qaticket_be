package com.qms.qms.dto.master;

import com.qms.qms.entity.Customer;

public record CustomerResponse(Long id, String name) {
    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(c.getId(), c.getName());
    }
}
