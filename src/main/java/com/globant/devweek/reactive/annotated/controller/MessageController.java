package com.globant.devweek.reactive.annotated.controller;


import com.globant.devweek.reactive.domain.Message;
import com.globant.devweek.reactive.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/annotated/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageRepository repository;

    @GetMapping
    public Flux<Message> getAllMessages() {
        return repository.findAll();
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Message>> getMessage(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Message> saveMessage(@RequestBody Message message) {
        return repository.save(message);
    }

    @PutMapping("{id}")
    public Mono<ResponseEntity<Message>> updateMessage(@PathVariable(value = "id") String id,
                                                       @RequestBody Message message) {
        return repository.findById(id)
                .flatMap(existingMessage -> {
                    existingMessage.setFrom("Annotated Controller");
                    existingMessage.setMessage(message.getMessage());
                    return repository.save(existingMessage);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteMessage(@PathVariable(value = "id") String id) {
        return repository.findById(id)
                .flatMap(existingMessage ->
                        repository.delete(existingMessage)
                                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public Mono<Void> deleteAllMessages() {
        return repository.deleteAll();
    }

}
