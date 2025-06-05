package com.example.demo.topic.entity;

import jakarta.persistence.*;

@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    // Getters and Setters
}
