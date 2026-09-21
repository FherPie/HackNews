package com.hacknews.andres.service;

import com.hacknews.andres.model.Story;

import java.util.Comparator;
import java.util.List;

public class StoryFilterService {

  private final WordCounter wordCounter = new WordCounter();

  public List<Story> filterLongTitles(List<Story> stories) {
    return stories.stream()
        .filter(story -> wordCounter.count(story.title()) > 5)
        .sorted(Comparator.comparingInt(Story::comments).reversed())
        .toList();
  }

  public List<Story> filterShortTitles(List<Story> stories) {
    return stories.stream()
        .filter(story -> wordCounter.count(story.title()) <= 5)
        .sorted(Comparator.comparingInt(Story::points).reversed())
        .toList();
  }

}