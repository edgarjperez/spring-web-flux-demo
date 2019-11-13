package com.globant.devweek.reactive;

import com.globant.devweek.reactive.domain.Message;
import com.globant.devweek.reactive.repository.MessageRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import reactor.core.publisher.Flux;

@SpringBootApplication
@EnableScheduling
public class ReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveApplication.class, args);
    }

    @Bean
    CommandLineRunner init(MessageRepository repository) {
        return args -> {
            Flux<Message> messageFlux = Flux.just(
                    new Message("", "Command Line Runner", "First Message"),
                    new Message("", "Command Line Runner", "Second Message"),
                    new Message("", "Command Line Runner", "Third Message")
                    ).flatMap(repository::save);

            messageFlux
                    .thenMany(repository.findAll())
                    .subscribe(System.out::println);
        };
    }

}
