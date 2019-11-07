package com.globant.devweek.reactive.controller;

import com.globant.devweek.reactive.domain.Message;
import com.globant.devweek.reactive.repository.MessageRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageRepository messageRepository;

    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping("/{id}")
    private Mono<Message> getEmployeeById(@PathVariable String id) {
        return messageRepository.findById(id);
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    private Flux<Message> getAllEmployees() {
        return Flux.interval(Duration.ofSeconds(1))
                .log()
                .map((val) -> new Message(String.format("Message %d", val)))
                .take(20);
    }

}
