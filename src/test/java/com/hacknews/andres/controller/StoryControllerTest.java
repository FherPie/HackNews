package com.hacknews.andres.controller;

import com.hacknews.andres.model.Story;
import com.hacknews.andres.service.HackerNewsService;
import com.hacknews.andres.service.StoryFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class StoryControllerTest {

  @Mock
  private HackerNewsService hackerNewsService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(new StoryController(hackerNewsService)).build();
  }

  @Test
  void shouldReturnStoriesForValidFilter() throws Exception {
    List<Story> stories = List.of(new Story(1, "Short title", 100, 5));
    when(hackerNewsService.findStories(StoryFilter.FIVE_OR_FEWER_WORDS)).thenReturn(stories);

    mockMvc.perform(get("/api/stories")
            .param("filter", "FIVE_OR_FEWER_WORDS"))
        .andExpect(status().isOk())
        .andExpect(content().json("""
            [{"number":1,"title":"Short title","points":100,"comments":5}]
            """));

    verify(hackerNewsService).findStories(StoryFilter.FIVE_OR_FEWER_WORDS);
  }

  @Test
  void shouldReturnBadRequestForInvalidFilter() throws Exception {
    mockMvc.perform(get("/api/stories").param("filter", "UNKNOWN"))
        .andExpect(status().isBadRequest());
  }
}
