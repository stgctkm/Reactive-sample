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
class RestTemplateController {

    RestTemplate restTemplate;

    RestTemplateController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/rest")
    List<StudentScore> rest() {
        long start = System.currentTimeMillis();

        ParameterizedTypeReference<List<Student>> studentType =
                new ParameterizedTypeReference<>() {
                };
        ParameterizedTypeReference<List<Score>> scoreType =
                new ParameterizedTypeReference<>() {
                };

        List<Student> students = restTemplate
                .exchange("http://localhost:8081/students/list", HttpMethod.GET, null, studentType)
                .getBody();

        List<StudentScore> studentScores = students.stream()
                .map(student -> {
                    String url = "http://localhost:8081/scores/" + student.id;
                    List<Score> scores = restTemplate
                            .exchange(url, HttpMethod.GET, null, scoreType)
                            .getBody();
                    return new StudentScore(student, scores);
                })
                .collect(Collectors.toList());

        System.out.println("rest: " + (System.currentTimeMillis() - start));

        return studentScores;
    }



    @GetMapping("/rest2")
    List<StudentScore> rest2() {
        long start = System.currentTimeMillis();

        ParameterizedTypeReference<List<Student>> studentType =
                new ParameterizedTypeReference<>() {
                };
        ParameterizedTypeReference<List<Score>> scoreType =
                new ParameterizedTypeReference<>() {
                };

        List<Student> students = restTemplate
                .exchange("http://localhost:8081/students/list", HttpMethod.GET, null, studentType)
                .getBody();

        String url = "http://localhost:8081/scores/" + ids(students);
        List<Score> scores = restTemplate
                .exchange(url, HttpMethod.GET, null, scoreType)
                .getBody();
        Map<Integer, List<Score>> scoreMap = scores.stream()
                .collect(Collectors.groupingBy(s -> s.id));

        List<StudentScore> studentScores = students.stream()
                .map(student -> new StudentScore(student, scoreMap.get(student.id)))
                .collect(Collectors.toList());

        System.out.println("rest2: " + (System.currentTimeMillis() - start));

        return studentScores;
    }


    private String ids(List<Student> students) {
        return students.stream()
                .map(s -> String.valueOf(s.id))
                .collect(Collectors.joining(","));
    }
}
