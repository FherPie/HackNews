package com.hacknews.andres.repository;

import com.hacknews.andres.model.UsageData;
import com.hacknews.andres.service.StoryFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UsageDataRepositoryIntegrationTest {

  @Autowired
  private UsageDataRepository usageDataRepository;

  @Test
  void shouldSaveAndRecoverUsageData() {
    Instant timestamp = Instant.now();
    UsageData saved = usageDataRepository.save(new UsageData(
        null,
        timestamp,
        StoryFilter.MORE_THAN_FIVE_WORDS,
        7,
        42
    ));

    UsageData recovered = usageDataRepository.findById(saved.id()).orElseThrow();

    assertEquals(saved.id(), recovered.id());
    assertEquals(
        timestamp.truncatedTo(ChronoUnit.MICROS),
        recovered.requestTimestamp()
    );
    assertEquals(StoryFilter.MORE_THAN_FIVE_WORDS, recovered.appliedFilter());
    assertEquals(7, recovered.resultCount());
    assertEquals(42, recovered.durationMs());
    assertTrue(recovered.id() > 0);
  }
}
