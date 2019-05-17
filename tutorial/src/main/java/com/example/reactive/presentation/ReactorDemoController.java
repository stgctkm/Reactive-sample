package com.example.reactive.presentation;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
class ReactorDemoController {

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<String> demo() {
        return Flux.interval(Duration.ofMillis(100))
                .map(it -> it + " " + LocalDateTime.now())
                .take(10)
                ;
    }

    @GetMapping("list")
    List<String> list() {
        return Arrays.asList("a", "b", "c");
    }
}
