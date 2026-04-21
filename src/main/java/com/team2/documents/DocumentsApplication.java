package com.team2.documents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = {
        "com.team2.documents.command.infrastructure.client",
        "com.team2.documents.infrastructure.client"
})
@EnableAsync
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class DocumentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentsApplication.class, args);
    }
}
