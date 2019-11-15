package com.globant.devweek.reactive.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    public Message(String from, String message) {
        this.from = from;
        this.message = message;
        this.createdAt = Instant.now();
    }

    @Id
    private String id;
    private String from;
    private String message;
    private Instant createdAt;

}
