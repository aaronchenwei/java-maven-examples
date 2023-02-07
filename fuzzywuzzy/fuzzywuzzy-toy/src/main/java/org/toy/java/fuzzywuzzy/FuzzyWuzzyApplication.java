package org.toy.java.fuzzywuzzy;

import com.google.common.base.Stopwatch;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;

@Slf4j
public class FuzzyWuzzyApplication {

  private static final int MAX_NUMBER_OF_ITERATIONS = 1024;

  private static final int MAX_NUMBER_OF_CHOICES = 100_000;

  private static final String KEY = "ABCDEFGHIJ";
  private static final List<String> choices = new ArrayList<>(MAX_NUMBER_OF_CHOICES);

  private static void initChoices() {
    choices.add(KEY);
    for (int i = 1; i < MAX_NUMBER_OF_CHOICES; i++) {
      choices.add(RandomStringUtils.randomAlphabetic(10));
    }
  }

  public static void main(String[] args) {
    initChoices();

    var result = FuzzySearch.extractTop(KEY, choices, 100);
    log.info("{}", result);

    for (int i = 0; i < MAX_NUMBER_OF_ITERATIONS; i++) {
      Stopwatch stopwatch = Stopwatch.createStarted();
      FuzzySearch.extractTop(KEY, choices, 100);
      stopwatch.stop();
      log.info("{} took: {}", i, stopwatch);
    }
  }
}
