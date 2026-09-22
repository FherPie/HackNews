# Hacker News Crawler

This project implements a small web crawler for the Hacker News front page.
It downloads the page HTML, extracts the first 30 stories, applies one of the
challenge filters, and records usage information for each filtering operation.

## Requirements

The application:

- scrapes `https://news.ycombinator.com/`;
- extracts the story number, title, points, and number of comments;
- supports filtering titles with more than five words;
- supports filtering titles with five words or fewer;
- records usage data for each request.

The scraper makes one HTTP request to the Hacker News front page. It does not
make individual requests for each story.

## Tech Stack

- Java 21
- Spring Boot 4.1.1
- Spring MVC
- Spring JDBC
- Jsoup 1.21.2
- H2
- Maven
- JUnit 5
- Mockito

The HTTP client uses Java's built-in `java.net.http.HttpClient`.

## Architecture

The request flow is:

```text
HTTP request
    |
    v
StoryController
    |
    v
HackerNewsService
    |--------------------> HackerNewsClient -> Hacker News HTML
    v
HackerNewsParser -> List<Story>
    |
    v
StoryFilterService
    |
    v
UsageDataRepository -> H2
```

Responsibilities are kept separate:

- `HackerNewsClient` performs the HTTP request and returns the HTML.
- `HackerNewsParser` uses Jsoup to parse the HTML and keeps the first 30
  stories.
- `StoryFilterService` applies the title word-count rules and sorting.
- `WordCounter` counts words and ignores tokens made only of symbols.
- `HackerNewsService` coordinates fetching, parsing, filtering, and usage
  recording.
- `UsageDataRepository` stores and reads usage records with `JdbcTemplate`.
- `StoryController` validates the filter query parameter and exposes the REST
  endpoint.

## Running the Application

### Prerequisites

- Java 21
- No separately installed Maven version is required; the project includes the
  Maven Wrapper.

### Windows

```powershell
./mvnw.cmd spring-boot:run
```

### Unix/macOS

```bash
./mvnw spring-boot:run
```

The application uses an in-memory H2 database configured in
`src/main/resources/application.yaml`.

## API

### Get filtered stories

**Method:** `GET`  
**URL:** `/api/stories`  
**Query parameter:** `filter`

Valid filter values:

- `MORE_THAN_FIVE_WORDS`
- `FIVE_OR_FEWER_WORDS`

Example:

```text
GET http://localhost:8080/api/stories?filter=FIVE_OR_FEWER_WORDS
```

Example response:

```json
[
  {
    "number": 1,
    "title": "Short title",
    "points": 100,
    "comments": 5
  }
]
```

Filter values are interpreted case-insensitively after trimming whitespace.
An unsupported value returns `400 Bad Request`.

## Filtering Rules

The two filters are implemented by `StoryFilterService`:

1. `MORE_THAN_FIVE_WORDS`
   - keeps titles with more than five words;
   - sorts the results by comments in descending order.
2. `FIVE_OR_FEWER_WORDS`
   - keeps titles with five words or fewer;
   - sorts the results by points in descending order.

`WordCounter` splits titles on whitespace. A token counts as a word when it
contains at least one letter or number. Tokens made only of symbols are
ignored.

For example:

```text
"This is - a self-explained example"
```

contains five words because `-` is ignored.

## Usage Tracking

After a successful filtering operation, `HackerNewsService` stores a record in
the `usage_data` H2 table with:

- `id`
- `request_timestamp`
- `applied_filter`
- `result_count`
- `duration_ms`

The timestamp is generated for the current operation. H2 is configured with
the following in-memory JDBC connection:

```text
JDBC URL: jdbc:h2:mem:andres;DB_CLOSE_DELAY=-1
User Name: sa
Password: <empty>
```

The H2 web console is enabled by configuration. While the application is
running, open the default console at:

```text
http://localhost:8080/h2-console
```

Use the JDBC URL, user name, and empty password above to connect. Because the
database is in memory, its data is lost when the application stops.

## Testing

The test suite includes:

- `WordCounter` unit tests, including symbol-only tokens;
- `StoryFilterService` unit tests for filtering and descending sorting;
- `HackerNewsParser` tests using the local `Hacker News.html` fixture;
- `HackerNewsClient` tests using a mocked `HttpClient`;
- `HackerNewsService` unit tests using Mockito;
- an H2 integration test for saving and recovering usage data;
- controller tests for a valid filter and an invalid filter;
- an application context test.

The unit tests do not connect to Hacker News. The parser uses local HTML and
the client tests mock the HTTP client.

### Windows

```powershell
./mvnw.cmd test
```

### Unix/macOS

```bash
./mvnw test
```

## Design Decisions

1. **Java `HttpClient`**  
   The client uses `java.net.http.HttpClient`, which is included in Java 21
   and avoids an additional HTTP client dependency.

2. **Jsoup for HTML parsing**  
   Jsoup is used by `HackerNewsParser` to select story rows and extract their
   fields from the Hacker News HTML.

3. **Separated responsibilities**  
   Fetching, parsing, filtering, orchestration, persistence, and HTTP
   presentation are implemented in separate classes. This keeps the rules
   testable without requiring real network calls.

4. **Local HTML fixture**  
   Parser tests use the checked-in `Hacker News.html` fixture, so they are
   deterministic and independent of Hacker News availability.

5. **H2 with `JdbcTemplate`**  
   H2 and Spring JDBC provide a lightweight persistence solution for the
   usage-tracking scope of this challenge without introducing an ORM.

## Assumptions

- Both sorting requirements mean descending order; this is implemented with
  reversed integer comparators.
- If the parser cannot find a story's rank, points, or comments, it uses `0`
  for that numeric field.
- A story with no comments link, including a `discuss` link, is treated as
  having zero comments.
- Usage data is written after a successful fetch, parse, and filter operation.
- The H2 database is intentionally in memory for this challenge and is not
  intended to provide data across application restarts.

## Possible Improvements

The current scope does not include:

- caching the Hacker News page between requests;
- making the Hacker News URL and HTTP timeout configurable;
- replacing the in-memory H2 database with persistent production storage;
- packaging the application in Docker.
