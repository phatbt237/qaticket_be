package com.qms.qms.service;

import org.springframework.stereotype.Service;

@Service
public class TicketCodeGenerator {
    public String generate(long nextSeq) {
        return String.format("I%05d", nextSeq);
    }
}
