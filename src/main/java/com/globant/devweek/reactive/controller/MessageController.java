package com.globant.devweek.reactive.controller;

import com.globant.devweek.reactive.domain.Message;
import com.globant.devweek.reactive.repository.MessageRepository;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageRepository messageRepository;

    public MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping("/{id}")
    private Mono<Message> getEmployeeById(@PathVariable String id) {
        return messageRepository.findMessageById(id);
    }

    @GetMapping(produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    private Flux<Message> getAllEmployees() {
        return messageRepository.findAllEmployees();
    }

}
