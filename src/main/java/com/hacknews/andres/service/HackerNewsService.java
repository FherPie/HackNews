package com.hacknews.andres.service;

import com.hacknews.andres.client.HackerNewsClient;
import com.hacknews.andres.model.Story;
import com.hacknews.andres.model.UsageData;
import com.hacknews.andres.parser.HackerNewsParser;
import com.hacknews.andres.repository.UsageDataRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class HackerNewsService {

  private final HackerNewsClient client;
  private final HackerNewsParser parser;
  private final StoryFilterService storyFilterService;
  private final UsageDataRepository usageDataRepository;

  public HackerNewsService(
      HackerNewsClient client,
      HackerNewsParser parser,
      StoryFilterService storyFilterService,
      UsageDataRepository usageDataRepository
  ) {
    this.client = client;
    this.parser = parser;
    this.storyFilterService = storyFilterService;
    this.usageDataRepository = usageDataRepository;
  }

  public List<Story> findStories(StoryFilter filter) {
    Instant requestTimestamp = Instant.now();
    long startNanos = System.nanoTime();

    String html = client.fetchHtml();
    List<Story> stories = parser.parse(html);
    List<Story> filteredStories = switch (filter) {
      case MORE_THAN_FIVE_WORDS -> storyFilterService.filterLongTitles(stories);
      case FIVE_OR_FEWER_WORDS -> storyFilterService.filterShortTitles(stories);
    };

    usageDataRepository.save(new UsageData(
        null,
        requestTimestamp,
        filter,
        filteredStories.size(),
        Duration.ofNanos(System.nanoTime() - startNanos).toMillis()
    ));

    return filteredStories;
  }
}
