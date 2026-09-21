package com.hacknews.andres.service;

public class WordCounter {

  public int count(String text) {
    String normalized = text.trim();

    if (normalized.isEmpty()) {
      return 0;
    }

    return normalized.split("\\s+").length;
  }
}
