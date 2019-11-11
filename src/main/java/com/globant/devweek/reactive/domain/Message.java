package com.globant.devweek.reactive.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    private String id;
    private String user;
    private String message;

    public Message(String message) {
        this.user = "default";
        this.message = message;
    }
}
