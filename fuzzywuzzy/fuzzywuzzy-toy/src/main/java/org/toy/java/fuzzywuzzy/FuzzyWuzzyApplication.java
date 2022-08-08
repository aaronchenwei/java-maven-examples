package org.toy.java.fuzzywuzzy;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class FuzzyWuzzyApplication {

    private static final List<String> choices = Arrays.asList(
        "google", "bing", "facebook", "linkedin", "twitter", "googleplus", "bingnews", "plexoogl");

    public static void main(String[] args) {
        for (int i = 0; i < 1024; i++) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            var result = FuzzySearch.extractTop("goolge", choices, 2);
            stopwatch.stop();
            log.info("{} took: {}", i, stopwatch);
        }
    }
}
