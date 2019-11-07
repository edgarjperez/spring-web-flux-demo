package com.globant.devweek.reactive.repository;


import com.globant.devweek.reactive.domain.Message;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MessageRepository {

    static Map<String, Message> messageData;

    static {
        messageData = new HashMap<>();
    }

    public static void createMessage(Message message) {
        messageData.put(message.getId(), message);
    }

    public Mono<Message> findMessageById(String id) {
        return Mono.just(messageData.get(id));
    }

    public Flux<Message> findAllEmployees() {
        return Flux.fromIterable(messageData.values());
    }

    public Mono<Message> updateEmployee(Message message) {
        Message existingMessage = messageData.get(message.getId());
        if (existingMessage != null) {
            existingMessage.setMessage(message.getMessage());
        }
        return Mono.just(existingMessage);
    }
}