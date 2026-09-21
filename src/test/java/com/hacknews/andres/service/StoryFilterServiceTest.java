package com.hacknews.andres.service;

import com.hacknews.andres.model.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StoryFilterServiceTest {

	@Test
	void contextLoads() {
	}


	private StoryFilterService service;

	@BeforeEach
	void setUp() {
		service = new StoryFilterService();
	}

	@Test
	void shouldFilterTitlesWithMoreThanFiveWordsAndSortByCommentsDescending() {

		List<Story> stories = List.of(
				new Story("This title has more than five words", 100, 20),
				new Story("Another interesting story with many words here", 50, 80),
				new Story("Java is great", 200, 100)
		);

		List<Story> result =
				service.filterLongTitles(stories);

		assertEquals(2, result.size());

		assertEquals(
				"Another interesting story with many words here",
				result.get(0).title()
		);

		assertEquals(80, result.get(0).comments());
	}

	@Test
	void shouldFilterTitlesWithFiveWordsOrLessAndSortByPointsDescending() {

		List<Story> stories = List.of(
				new Story("Java is great", 200, 10),
				new Story("Learning Spring Boot today", 500, 5),
				new Story("This title has more than five words", 100, 100)
		);

		List<Story> result =
				service.filterShortTitles(stories);

		assertEquals(2, result.size());

		assertEquals(
				"Learning Spring Boot today",
				result.get(0).title()
		);

		assertEquals(500, result.get(0).points());
	}


	@Test
	void shouldNotIncludeTitleWithExactlyFiveWordsInLongTitles() {

		Story story = new Story(
				"Java Spring Boot is great",
				100,
				20
		);

		List<Story> result =
				service.filterLongTitles(List.of(story));

		assertTrue(result.isEmpty());
	}


	@Test
	void shouldReturnEmptyListWhenFilteringEmptyList() {

		List<Story> longTitles =
				service.filterLongTitles(List.of());

		List<Story> shortTitles =
				service.filterShortTitles(List.of());

		assertTrue(longTitles.isEmpty());
		assertTrue(shortTitles.isEmpty());
	}

	@Test
	void shouldKeepAllStoriesWhenCommentsAreEqual() {

		Story first = new Story(
				"This is a title with many words",
				50,
				100
		);

		Story second = new Story(
				"Another title that contains many words here",
				80,
				100
		);

		List<Story> result =
				service.filterLongTitles(List.of(first, second));

		assertEquals(2, result.size());
		assertTrue(result.contains(first));
		assertTrue(result.contains(second));
	}

}



