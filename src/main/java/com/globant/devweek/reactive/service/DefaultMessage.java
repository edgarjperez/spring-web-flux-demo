package com.globant.devweek.reactive.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globant.devweek.reactive.domain.Message;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@RequiredArgsConstructor
public class DefaultMessage {

    //private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaSender<String, String> kafkaSender;

    @Value("${kafka.topic}")
    private String topic;

    public void saveMessage(Message message) {
        kafkaSender.send(Mono.just(messageToSenderRecord(message)))
                .next()
                .log()
                .subscribe();
    }

    private SenderRecord<String, String, String> messageToSenderRecord(Message message) {
        String messageJsonStr;

        try {
            messageJsonStr = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return SenderRecord.create(new ProducerRecord<>(topic, messageJsonStr), message.getId());
    }
}
