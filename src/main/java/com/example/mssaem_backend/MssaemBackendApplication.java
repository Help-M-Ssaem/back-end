package com.example.mssaem_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableCaching
@EnableJpaAuditing
@SpringBootApplication
public class MssaemBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MssaemBackendApplication.class, args);
    }

}
