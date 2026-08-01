package com.qms.qms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class QmsBackendApplication {
    public static void main(String[] args) {
        // Pin the JVM clock to Vietnam time regardless of host/container default (containers
        // default to UTC), so LocalDateTime.now() and DB timestamps stay in the business timezone.
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SpringApplication.run(QmsBackendApplication.class, args);
    }
}
