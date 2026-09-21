package com.hacknews.andres.service;

import java.util.Arrays;

public class WordCounter {

  public int count(String text) {
    String normalized = text.trim();

    if (normalized.isEmpty()) {
      return 0;
    }

    return (int) Arrays.stream(normalized.split("\\s+"))
        .filter(token -> token.matches(".*[\\p{L}\\p{N}].*"))
        .count();
  }
}
