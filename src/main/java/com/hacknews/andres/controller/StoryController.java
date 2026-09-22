package com.hacknews.andres.controller;

import com.hacknews.andres.model.Story;
import com.hacknews.andres.service.HackerNewsService;
import com.hacknews.andres.service.StoryFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/stories")
public class StoryController {

  private final HackerNewsService hackerNewsService;

  public StoryController(HackerNewsService hackerNewsService) {
    this.hackerNewsService = hackerNewsService;
  }

  @GetMapping
  public List<Story> getStories(@RequestParam String filter) {
    return hackerNewsService.findStories(parseFilter(filter));
  }

  private StoryFilter parseFilter(String filter) {
    try {
      return StoryFilter.valueOf(filter.trim().toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException exception) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Unsupported filter: " + filter,
          exception
      );
    }
  }
}
