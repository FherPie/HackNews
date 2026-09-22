package com.hacknews.andres.service;

import com.hacknews.andres.client.HackerNewsClient;
import com.hacknews.andres.model.Story;
import com.hacknews.andres.model.UsageData;
import com.hacknews.andres.parser.HackerNewsParser;
import com.hacknews.andres.repository.UsageDataRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HackerNewsServiceTest {

  @Mock
  private HackerNewsClient client;

  @Mock
  private HackerNewsParser parser;

  @Mock
  private StoryFilterService storyFilterService;

  @Mock
  private UsageDataRepository usageDataRepository;

  @InjectMocks
  private HackerNewsService hackerNewsService;

  @Test
  void shouldCoordinateClientParserAndLongTitleFilter() {
    String html = "<html>stories</html>";
    List<Story> parsedStories = List.of(new Story("A long title with six words", 10, 20));
    List<Story> filteredStories = List.of(parsedStories.getFirst());

    when(client.fetchHtml()).thenReturn(html);
    when(parser.parse(html)).thenReturn(parsedStories);
    when(storyFilterService.filterLongTitles(parsedStories)).thenReturn(filteredStories);

    assertEquals(
        filteredStories,
        hackerNewsService.findStories(StoryFilter.MORE_THAN_FIVE_WORDS)
    );

    verify(client).fetchHtml();
    verify(parser).parse(html);
    verify(storyFilterService).filterLongTitles(parsedStories);
  }

  @Test
  void shouldSelectShortTitleFilter() {
    List<Story> parsedStories = List.of(new Story("Short title", 100, 2));
    when(client.fetchHtml()).thenReturn("html");
    when(parser.parse("html")).thenReturn(parsedStories);
    when(storyFilterService.filterShortTitles(parsedStories)).thenReturn(parsedStories);

    hackerNewsService.findStories(StoryFilter.FIVE_OR_FEWER_WORDS);

    verify(storyFilterService).filterShortTitles(parsedStories);
  }

  @Test
  void shouldRegisterUsageDataForOperation() {
    List<Story> filteredStories = List.of(new Story("Short title", 100, 2));
    when(client.fetchHtml()).thenReturn("html");
    when(parser.parse("html")).thenReturn(filteredStories);
    when(storyFilterService.filterShortTitles(filteredStories)).thenReturn(filteredStories);

    hackerNewsService.findStories(StoryFilter.FIVE_OR_FEWER_WORDS);

    ArgumentCaptor<UsageData> usageCaptor = ArgumentCaptor.forClass(UsageData.class);
    verify(usageDataRepository).save(usageCaptor.capture());

    UsageData usageData = usageCaptor.getValue();
    assertEquals(StoryFilter.FIVE_OR_FEWER_WORDS, usageData.appliedFilter());
    assertEquals(1, usageData.resultCount());
    assertTrue(usageData.durationMs() >= 0);
  }
}
