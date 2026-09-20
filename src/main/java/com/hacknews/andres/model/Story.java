package com.hacknews.andres.model;

public record Story(
    String title,
    int points,
    int comments
) {}