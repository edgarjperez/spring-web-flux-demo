package com.globant.devweek.reactive.controller.functional;

import com.globant.devweek.reactive.domain.Message;
import com.globant.devweek.reactive.repository.MessageRepository;
import com.globant.devweek.reactive.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;

@Component
@RequiredArgsConstructor
public class MessageHandler {

    private final MessageRepository repository;
    private final KafkaService kafkaService;
    private static final String from = "Functional Endpoint";

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
                .flatMap(message -> {

                    return ServerResponse.status(CREATED)
                            .contentType(APPLICATION_JSON)
                            .body(repository.save(new Message(from, message.getMessage()))
                                            .doOnSuccess(kafkaService::saveMessage)
                                    , Message.class);
                });
    }

    public Mono<ServerResponse> updateMessage(ServerRequest request) {
        Mono<Message> existingMessageMono = repository.findById(request.pathVariable("id"));
        return request.bodyToMono(Message.class)
                .zipWith(existingMessageMono, ((message, existingMessage) ->
                        new Message( from, message.getMessage())))
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

    public Mono<ServerResponse> getMessagesStream(ServerRequest request) {
        Flux<Message> messageEventFlux = kafkaService.getConnectableFlux()
                .map(stringServerSentEvent -> kafkaService.jsonToMessage(stringServerSentEvent.data()));
        return ServerResponse.ok()
                .contentType(APPLICATION_STREAM_JSON)
                .body(messageEventFlux, Message.class);
    }

}
