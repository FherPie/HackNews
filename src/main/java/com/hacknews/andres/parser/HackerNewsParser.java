package com.hacknews.andres.parser;

import com.hacknews.andres.model.Story;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HackerNewsParser {

  public List<Story> parse(String html) {
    if (html == null || html.isBlank()) {
      return List.of();
    }

    Document document = Jsoup.parse(html);
    Elements storyRows = document.select("tr.athing");

    List<Story> stories = new ArrayList<>();
    for (Element storyRow : storyRows) {
      if (stories.size() >= 30) {
        break;
      }

      Element metadataRow = storyRow.nextElementSibling();

      stories.add(new Story(
          parseNumber(storyRow.selectFirst(".rank")),
          parseTitle(storyRow.selectFirst(".titleline > a")),
          parsePoints(metadataRow != null ? metadataRow.selectFirst(".score") : null),
          parseComments(metadataRow)
      ));
    }

    return stories;
  }

  private int parseNumber(Element rankElement) {
    if (rankElement == null) {
      return 0;
    }

    String text = rankElement.text();
    if (text == null || text.isBlank()) {
      return 0;
    }

    try {
      return Integer.parseInt(text.replace(".", "").trim());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private String parseTitle(Element titleElement) {
    if (titleElement == null) {
      return "";
    }

    return titleElement.text();
  }

  private int parsePoints(Element scoreElement) {
    if (scoreElement == null) {
      return 0;
    }

    String text = scoreElement.text();
    return extractDigits(text);
  }

  private int parseComments(Element metadataRow) {
    if (metadataRow == null) {
      return 0;
    }

    for (Element link : metadataRow.select("a[href*='item?id=']")) {
      String text = link.text();
      if (text == null || text.isBlank()) {
        continue;
      }

      String normalized = text.trim();
      if (normalized.equalsIgnoreCase("discuss")) {
        return 0;
      }

      if (normalized.toLowerCase().contains("comment")) {
        return extractDigits(normalized);
      }
    }

    return 0;
  }

  private int extractDigits(String text) {
    if (text == null || text.isBlank()) {
      return 0;
    }

    String digits = text.replaceAll("[^0-9]", "");
    if (digits.isEmpty()) {
      return 0;
    }

    return Integer.parseInt(digits);
  }
}
