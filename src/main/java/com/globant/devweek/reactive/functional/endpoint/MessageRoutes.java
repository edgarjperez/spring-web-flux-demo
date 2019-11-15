package com.globant.devweek.reactive.functional.endpoint;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class MessageRoutes {

    @Bean
    RouterFunction<ServerResponse> routes(MessageHandler handler) {
        return nest(path("/functional/messages"),
                nest(accept(APPLICATION_JSON).or(contentType(APPLICATION_JSON).or(contentType(APPLICATION_STREAM_JSON))),
                        route(GET("/"), handler::getAllMessages)
                                .andRoute(method(POST), handler::saveMessage)
                                .andRoute(DELETE("/"), handler::deleteAllMessage)
                                .andRoute(GET("/stream"), handler::getMessagesStream)
                                .andNest(path("/{id}"),
                                        route(method(GET), handler::getMessage)
                                                .andRoute(method(PUT), handler::updateMessage)
                                                .andRoute(method(DELETE), handler::deleteMessage)
                                )
                )
        );
    }

}
