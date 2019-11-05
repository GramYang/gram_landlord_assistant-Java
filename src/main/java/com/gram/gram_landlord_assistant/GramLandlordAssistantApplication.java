package com.gram.gram_landlord_assistant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gram.gram_landlord_assistant.mapper")
public class GramLandlordAssistantApplication {
    public static void main(String[] args) {
        SpringApplication.run(GramLandlordAssistantApplication.class, args);
    }
}
