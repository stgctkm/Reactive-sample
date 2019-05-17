package com.example.reactive.domain;

public class Student {
    public int id;
    public String name;

    @Override
    public String toString() {
        return id + ":" + name;
    }

    public Student(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

