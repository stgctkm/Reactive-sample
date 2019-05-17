package com.example.reactive.presentation;


import com.example.reactive.domain.Student;
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
class FrontController {

    Student[] students = {new Student(1, "武藤"), new Student(2, "三好")};

    @GetMapping(value = "-flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Student> demo() {
        return Flux.interval(Duration.ofMillis(100))
                .map(it -> students[it.intValue()])
                .take(students.length)
                ;
    }

    @GetMapping("list")
    List<Student> list() throws InterruptedException {
        Thread.sleep(students.length * 100);
        return Arrays.asList(students);
    }
}
