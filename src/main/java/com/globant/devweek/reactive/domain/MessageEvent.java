package com.globant.devweek.reactive.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MessageEvent {

    private Long messageId;
    private String messageType;
}
