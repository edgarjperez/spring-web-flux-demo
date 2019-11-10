package com.globant.devweek.reactive.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globant.devweek.reactive.domain.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.kafka.receiver.KafkaReceiver;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaReceiver<String, String> kafkaReceiver;
    private ConnectableFlux<ServerSentEvent<String>> eventPublisher;
    private final ObjectMapper objectMapper;


    @PostConstruct
    public void init() {
        eventPublisher = kafkaReceiver.receive()
                .map(consumerRecord -> ServerSentEvent.builder(consumerRecord.value()).build())
                .publish();
        eventPublisher.connect();
    }

    public ConnectableFlux<ServerSentEvent<String>> getEventPublisher() {
        return eventPublisher;
    }

    public Message jsonToMessage(String jsonStr) {
        Message message;
        try {
            message = objectMapper.readValue(jsonStr, Message.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
        return message;
    }

    public ServerSentEvent<Message> matchToServerSentEvent(Message message) {
        return ServerSentEvent.<Message>builder()
                .data(message)
                .build();
    }
}
