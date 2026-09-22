package com.hacknews.andres.model;

import com.hacknews.andres.service.StoryFilter;

import java.time.Instant;

public record UsageData(
    Long id,
    Instant requestTimestamp,
    StoryFilter appliedFilter,
    int resultCount,
    long durationMs
) {
}
