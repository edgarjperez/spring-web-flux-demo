package com.globant.devweek.reactive.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globant.devweek.reactive.domain.Message;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaReceiver<String, String> kafkaReceiver;
    private final KafkaSender<String, String> kafkaSender;
    private ConnectableFlux<ServerSentEvent<String>> connectableFlux;
    private final ObjectMapper objectMapper;

    @Value("${kafka.topic}")
    private String topic;


    @PostConstruct
    public void init() {
        connectableFlux = kafkaReceiver.receive()
                .map(consumerRecord -> ServerSentEvent.builder(consumerRecord.value()).build())
                .publish();
        connectableFlux.connect();
    }

    public ConnectableFlux<ServerSentEvent<String>> getConnectableFlux() {
        return connectableFlux;
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

    public void saveMessage(Message message) {
        kafkaSender.send(Mono.just(messageToSenderRecord(message)))
                .next()
                .log()
                .subscribe();
    }

    public Mono<ServerResponse> messageToServerSentEvent(Message message) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(message, Message.class);
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
