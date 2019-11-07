package com.globant.devweek.reactive.service;

import com.globant.devweek.reactive.domain.Message;
import com.globant.devweek.reactive.repository.MessageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Component
public class MessageCreatorServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private AtomicInteger counter = new AtomicInteger(0);

    public MessageCreatorServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void deliverMessage(String user, String message) {
        MessageRepository.createMessage(new Message(message));
    }

    @Scheduled(fixedRate = 5000, initialDelay = 5000)
    public void deliverMessage() {
        log.info("Delivering test message");
        MessageRepository.createMessage(new Message(String.format("Test Message %d", counter.incrementAndGet())));
    }
}
