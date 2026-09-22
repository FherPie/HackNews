package com.hacknews.andres.repository;

import com.hacknews.andres.model.UsageData;
import com.hacknews.andres.service.StoryFilter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class UsageDataRepository {

  private final JdbcTemplate jdbcTemplate;

  public UsageDataRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  public UsageData save(UsageData usageData) {
    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      PreparedStatement statement = connection.prepareStatement(
          """
              INSERT INTO usage_data (
                  request_timestamp, applied_filter, result_count, duration_ms
              ) VALUES (?, ?, ?, ?)
              """,
          Statement.RETURN_GENERATED_KEYS
      );
      statement.setTimestamp(1, Timestamp.from(usageData.requestTimestamp()));
      statement.setString(2, usageData.appliedFilter().name());
      statement.setInt(3, usageData.resultCount());
      statement.setLong(4, usageData.durationMs());
      return statement;
    }, keyHolder);

    Number generatedId = keyHolder.getKey();
    if (generatedId == null) {
      throw new IllegalStateException("Usage data insert did not return an id");
    }
    Long id = generatedId.longValue();

    return new UsageData(
        id,
        usageData.requestTimestamp(),
        usageData.appliedFilter(),
        usageData.resultCount(),
        usageData.durationMs()
    );
  }

  public Optional<UsageData> findById(long id) {
    List<UsageData> records = jdbcTemplate.query(
        """
            SELECT id, request_timestamp, applied_filter, result_count, duration_ms
            FROM usage_data
            WHERE id = ?
            """,
        (resultSet, rowNum) -> new UsageData(
            resultSet.getLong("id"),
            resultSet.getTimestamp("request_timestamp").toInstant(),
            StoryFilter.valueOf(resultSet.getString("applied_filter")),
            resultSet.getInt("result_count"),
            resultSet.getLong("duration_ms")
        ),
        id
    );

    return records.stream().findFirst();
  }
}
