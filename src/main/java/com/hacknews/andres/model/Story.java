package com.hacknews.andres.model;

public record Story(int number, String title, int points, int comments) {

  public Story(String title, int points, int comments) {
    this(0, title, points, comments);
  }
}
