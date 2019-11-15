package com.globant.devweek.reactive.service;

import com.globant.devweek.reactive.domain.Message;
import com.globant.devweek.reactive.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Component
@RequiredArgsConstructor
public class MessageScheduler implements MessageService {

    private final MessageRepository messageRepository;
    private final KafkaService kafkaService;
    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void deliverMessage(String user, String message) {
    }

    @Scheduled(fixedRate = 5000, initialDelay = 5000)
    public void deliverMessage() {
        log.info("Delivering test message");
        messageRepository.save(
                new Message(
                        "From Scheduler",
                        String.format("Test Message %d", counter.incrementAndGet()))
        ).doOnSuccess(kafkaService::saveMessage).subscribe();
    }
}
