package com.hacknews.andres.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WordCounterTest {

  private final WordCounter wordCounter = new WordCounter();


  @ParameterizedTest
  @CsvSource({
      "'Hola que-tal como estas', 4",
      "'Flet 1.0 – Build cross-platform apps in Python', 7",
      "'Cekura (YC F24) Is Hiring', 5",
      "'Fujitsu launches made-in-Japan next-generation CPU FUJITSU-MONAKA', 6"
  })
  void shouldCountWordsSeparatedByWhitespace(
      String title,
      int expected) {

    assertEquals(expected, wordCounter.count(title));
  }

  @ParameterizedTest
  @CsvSource({
      "'', 0",
      "'   ', 0",
      "'Java', 1",
      "'Java   Spring    Boot', 3",
      "' Java Spring Boot ', 3"
  })
  void shouldHandleWhitespaceCorrectly(
      String title,
      int expected) {

    assertEquals(expected, wordCounter.count(title));
  }

  @Test
  void shouldIgnoreSymbolOnlyTokensWhenCountingWords() {

    int result = wordCounter.count(
        "This is - a self-explained example"
    );

    assertEquals(5, result);
  }
}
