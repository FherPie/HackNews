package com.hacknews.andres.client;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class HackerNewsClient {

  private static final URI HACKER_NEWS_URI = URI.create("https://news.ycombinator.com/");
  private static final Duration TIMEOUT = Duration.ofSeconds(10);

  private final HttpClient httpClient;

  public HackerNewsClient() {
    this(HttpClient.newBuilder()
        .connectTimeout(TIMEOUT)
        .build());
  }

  public HackerNewsClient(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public String fetchHtml() {
    HttpRequest request = HttpRequest.newBuilder(HACKER_NEWS_URI)
        .timeout(TIMEOUT)
        .GET()
        .build();

    try {
      HttpResponse<String> response = httpClient.send(
          request,
          HttpResponse.BodyHandlers.ofString()
      );

      if (response.statusCode() < 200 || response.statusCode() >= 300) {
        throw new IllegalStateException(
            "Hacker News returned HTTP status " + response.statusCode()
        );
      }

      return response.body();
    } catch (IOException e) {
      throw new IllegalStateException("Could not download Hacker News HTML", e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Hacker News request was interrupted", e);
    }
  }
}
