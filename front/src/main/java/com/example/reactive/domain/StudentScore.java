package com.example.reactive.domain;

import java.util.List;

public class StudentScore {
    public Student student;
    public List<Score> scores;

    public StudentScore(Student student, List<Score> scores) {
        this.student = student;
        this.scores = scores;
    }
}
