package com.example.upload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class ImageUploadApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImageUploadApplication.class, args);
    }
}