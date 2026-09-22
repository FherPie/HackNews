package com.hacknews.andres.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HackerNewsClientTest {

  @Mock
  private HttpClient httpClient;

  @Mock
  private HttpResponse<String> httpResponse;

  private HackerNewsClient client;

  @BeforeEach
  void setUp() {
    client = new HackerNewsClient(httpClient);
  }

  @Test
  void shouldReturnHtmlBodyWhenHackerNewsRespondsSuccessfully() throws Exception {
    String expectedHtml = "<html><body>Hacker News</body></html>";
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);
    when(httpResponse.statusCode()).thenReturn(200);
    when(httpResponse.body()).thenReturn(expectedHtml);

    String actualHtml = client.fetchHtml();

    assertEquals(expectedHtml, actualHtml);
  }

  @Test
  void shouldSendGetRequestToHackerNewsUrlAndUseTimeout() throws Exception {
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);
    when(httpResponse.statusCode()).thenReturn(200);
    when(httpResponse.body()).thenReturn("<html></html>");

    client.fetchHtml();

    ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
    verify(httpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));

    HttpRequest request = requestCaptor.getValue();
    assertEquals("GET", request.method());
    assertEquals("https://news.ycombinator.com/", request.uri().toString());
    assertTrue(request.timeout().isPresent());
    assertEquals(Duration.ofSeconds(10), request.timeout().orElseThrow());
  }

  @Test
  void shouldThrowIllegalStateExceptionWhenStatusIsNotSuccessful() throws Exception {
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);
    when(httpResponse.statusCode()).thenReturn(500);

    IllegalStateException exception = assertThrows(
        IllegalStateException.class,
        client::fetchHtml
    );

    assertEquals("Hacker News returned HTTP status 500", exception.getMessage());
  }

  @Test
  void shouldWrapIOExceptionInIllegalStateException() throws Exception {
    IOException ioException = new IOException("network down");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(ioException);

    IllegalStateException exception = assertThrows(
        IllegalStateException.class,
        client::fetchHtml
    );

    assertEquals("Could not download Hacker News HTML", exception.getMessage());
    assertSame(ioException, exception.getCause());
  }

  @Test
  void shouldWrapInterruptedExceptionAndRestoreInterruptStatus() throws Exception {
    InterruptedException interruptedException =
        new InterruptedException("interrupted");

    when(httpClient.send(
        any(HttpRequest.class),
        any(HttpResponse.BodyHandler.class)))
        .thenThrow(interruptedException);

    assertFalse(Thread.currentThread().isInterrupted());

    try {
      IllegalStateException exception = assertThrows(
          IllegalStateException.class,
          client::fetchHtml
      );

      assertEquals(
          "Hacker News request was interrupted",
          exception.getMessage()
      );
      assertSame(interruptedException, exception.getCause());
      assertTrue(Thread.currentThread().isInterrupted());

    } finally {
      Thread.interrupted();
    }
  }
}
