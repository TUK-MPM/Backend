package com.zikk.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ZikkBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZikkBackendApplication.class, args);
    }

}
