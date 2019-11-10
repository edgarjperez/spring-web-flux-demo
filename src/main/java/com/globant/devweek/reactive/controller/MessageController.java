package com.globant.devweek.reactive.controller;

import com.globant.devweek.reactive.domain.Message;
import com.globant.devweek.reactive.repository.MessageRepository;
import com.globant.devweek.reactive.service.DefaultMessage;
import com.globant.devweek.reactive.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/messages")
public class MessageController {

    private final MessageRepository messageRepository;
    private final DefaultMessage defaultMessage;
    private final KafkaService kafkaService;

    @GetMapping("/{id}")
    private Mono<Message> getEmployeeById(@PathVariable String id) {
        return messageRepository.findById(id);
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    private Flux<ServerSentEvent<Message>> getAllMessages() {
        return kafkaService.getEventPublisher()
                .map(stringServerSentEvent -> kafkaService.jsonToMessage(stringServerSentEvent.data()))
                .map(kafkaService::matchToServerSentEvent);
    }

    @PostMapping
    private void saveMessage(@RequestBody Message message) {
        defaultMessage.saveMessage(new Message(message.getMessage()));
    }

}
