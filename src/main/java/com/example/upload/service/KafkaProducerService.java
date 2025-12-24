package com.example.upload.service;


import com.example.upload.dto.ImageProcessingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, ImageProcessingEvent> kafkaTemplate;

    @Value("${kafka.topics.image-processing}")
    private String imageProcessingTopic;

    /**
     * Send image processing event to Kafka
     */
    public void sendImageProcessingEvent(ImageProcessingEvent event) {
        log.info("Sending image processing event to Kafka: imageId={}", event.getImageId());

        CompletableFuture<SendResult<String, ImageProcessingEvent>> future = 
                kafkaTemplate.send(imageProcessingTopic, event.getImageId(), event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send message to Kafka: imageId={}", event.getImageId(), ex);
            } else {
                log.info("Message sent successfully to Kafka: imageId={}, partition={}, offset={}", 
                        event.getImageId(), 
                        result.getRecordMetadata().partition(), 
                        result.getRecordMetadata().offset());
            }
        });
    }
}