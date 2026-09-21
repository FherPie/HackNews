package com.hacknews.andres.parser;

import com.hacknews.andres.model.Story;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HackerNewsParserTest {


  @Test
  void shouldExtractFirstThirtyStories() throws IOException {

    String html = loadFixture("Hacker News.html");

    HackerNewsParser parser = new HackerNewsParser();

    List<Story> stories = parser.parse(html);

    assertEquals(30, stories.size());
  }

  private String loadFixture(String fileName) throws IOException {

    try (var inputStream = getClass()
        .getClassLoader()
        .getResourceAsStream(fileName)) {

      assertNotNull(inputStream);

      return new String(
          inputStream.readAllBytes(),
          StandardCharsets.UTF_8
      );
    }
  }
}