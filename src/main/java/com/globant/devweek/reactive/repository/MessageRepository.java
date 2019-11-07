package com.globant.devweek.reactive.repository;

import com.globant.devweek.reactive.domain.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MessageRepository extends ReactiveMongoRepository<Message, String> {
}
