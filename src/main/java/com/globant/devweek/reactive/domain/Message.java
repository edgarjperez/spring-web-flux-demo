package com.globant.devweek.reactive.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private String id;
    private String user;
    private String message;

    public Message(String message) {
        this.id = UUID.randomUUID().toString();
        this.user = "default";
        this.message = message;
    }
}
