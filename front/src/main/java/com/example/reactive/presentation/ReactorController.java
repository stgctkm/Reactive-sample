package com.example.reactive.presentation;

import com.example.reactive.domain.Score;
import com.example.reactive.domain.Student;
import com.example.reactive.domain.StudentScore;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
class ReactorController {

    WebClient webClient;

    ReactorController(WebClient webClient) {
        this.webClient = webClient;
    }


    @GetMapping(value = "/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<StudentScore> getAsFlux() {
        long start = System.currentTimeMillis();

        Flux<Student> students = webClient.get()
                .uri("localhost:8081/students/flux")
                .retrieve()
                .bodyToFlux(Student.class);

        Flux<StudentScore> studentScore = students.flatMap(student ->
                webClient.get()
                        .uri("localhost:8081/scores/" + student.id)
                        .retrieve()
                        .bodyToFlux(Score.class)
                        .collectList()
                        .map(scores -> new StudentScore(student, scores)));

        return studentScore.doOnComplete(() ->
                System.out.println("flux: " + (System.currentTimeMillis() - start)));
    }

    @GetMapping("/map")
    Mono<Map<Student, List<Score>>> getAsMap() {
        Flux<Student> students = webClient.get()
                .uri("localhost:8081/students/flux")
                .retrieve()
                .bodyToFlux(Student.class);

        Mono<Map<Student, List<Score>>> map = students.flatMap(student ->
                webClient.get()
                        .uri("localhost:8081/scores/" + student.id)
                        .retrieve()
                        .bodyToFlux(Score.class)
                        .collectList()
                        .map(scores -> Tuples.of(student, scores)))
                .collectMap(Tuple2::getT1, Tuple2::getT2);

        return map;
    }


    @GetMapping(value = "/flux2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Mono<List<StudentScore>> flux2() {
        long start = System.currentTimeMillis();

        Mono<List<Student>> students = webClient.get()
                .uri("localhost:8081/students/flux")
                .retrieve()
                .bodyToFlux(Student.class)
                .collectList();

        Mono<List<StudentScore>> studentScore = students.flatMap(studentList -> webClient.get()
                .uri("localhost:8081/scores/" + ids(studentList))
                .retrieve()
                .bodyToFlux(Score.class)
                .collectList()
                .map(scores -> {
                    Map<Integer, List<Score>> scoreMap = scores.stream()
                            .collect(Collectors.groupingBy(s -> s.id));
                    return studentList.stream()
                            .map(s -> new StudentScore(s, scoreMap.get(s.id)))
                            .collect(Collectors.toList());
                }));

        return studentScore
                .doOnSuccess(x -> System.out.println("flux2: " + (System.currentTimeMillis() - start)));
    }

    @GetMapping(value = "/flux3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<StudentScore> flux3() {
        long start = System.currentTimeMillis();

        Flux<Student> students = webClient.get()
                .uri("localhost:8081/students/flux")
                .retrieve()
                .bodyToFlux(Student.class)
                .cache();

        Flux<StudentScore> studentScore = webClient.post()
                .uri("localhost:8081/scores/flux")
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(students.map(s -> s.id), Integer.class)
                .retrieve()
                .bodyToFlux(Score.class)
                .collectList()
                .map(scores -> scores.stream().collect(Collectors.groupingBy(s -> s.id)))
                .flatMapMany(scoreMap -> students.map(student -> new StudentScore(student, scoreMap.get(student.id))));

        return studentScore.doOnComplete(() ->
                System.out.println("flux3: " + (System.currentTimeMillis() - start)));
    }

    private String ids(List<Student> students) {
        return students.stream()
                .map(s -> String.valueOf(s.id))
                .collect(Collectors.joining(","));
    }
}
