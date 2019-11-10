package com.globant.devweek.reactive.handler;

import com.globant.devweek.reactive.domain.Message;
import com.globant.devweek.reactive.domain.MessageEvent;
import com.globant.devweek.reactive.repository.MessageRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;

@Component
public class MessageHandler {

    private MessageRepository repository;

    public MessageHandler(MessageRepository repository) {
        this.repository = repository;
    }

    public Mono<ServerResponse> getAllMessages(ServerRequest request) {
        Flux<Message> messages = repository.findAll();
        return ServerResponse.ok()
                .contentType(APPLICATION_JSON)
                .body(messages, Message.class);
    }

    public Mono<ServerResponse> getMessage(ServerRequest request) {
        return repository.findById(request.pathVariable("id"))
                .flatMap(message ->
                        ServerResponse.ok()
                                .contentType(APPLICATION_JSON)
                                .body(message, Message.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> saveMessage(ServerRequest request) {
        return request.bodyToMono(Message.class)
                .flatMap(message ->
                        ServerResponse.status(CREATED)
                                .contentType(APPLICATION_JSON)
                                .body(repository.save(message), Message.class));
    }

    public Mono<ServerResponse> updateMessage(ServerRequest request) {
        Mono<Message> existingMessageMono = repository.findById(request.pathVariable("id"));
        return request.bodyToMono(Message.class)
                .zipWith(existingMessageMono, ((message, existingMessage) ->
                        new Message(message.getMessage())))
                .flatMap(message -> ServerResponse.ok()
                        .contentType(APPLICATION_JSON)
                        .body(repository.save(message), Message.class))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteMessage(ServerRequest request) {
        Mono<Message> messageMono = repository.findById(request.pathVariable("id"));
        return messageMono
                .flatMap(message -> ServerResponse.ok()
                        .build(repository.delete(message))
                ).switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteAllMessage(ServerRequest request) {
        return ServerResponse.ok()
                .build(repository.deleteAll());
    }

    public Mono<ServerResponse> getMessageEvents(ServerRequest request) {
        Flux<MessageEvent> messageEventFlux = Flux.interval(Duration.ofSeconds(1))
                .map(val -> new MessageEvent(val, "Message Event")
                );
        return ServerResponse.ok()
                .contentType(TEXT_EVENT_STREAM)
                .body(messageEventFlux, MessageEvent.class);
    }

}
