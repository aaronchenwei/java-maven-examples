package org.toy.java.fuzzywuzzy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.ratios.SimpleRatio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FuzzySearchTest {

  private static final List<String> choices = Arrays.asList(
    "google", "bing", "facebook", "linkedin", "twitter", "googleplus", "bingnews", "plexoogl");

  private static final List<String> moreChoices = Arrays.asList(
    "Atlanta Falcons", "New York Jets", "New York Giants", "Dallas Cowboys");


  @DisplayName("Test Ratio")
  @Test
  void testRatio() {
    assertEquals(76, FuzzySearch.ratio("mysmilarstring", "mymostsimilarstsdsdring"), 2);
    assertEquals(72, FuzzySearch.ratio("mysmilarstring", "myawfullysimilarstirng"), 2);
    assertEquals(97, FuzzySearch.ratio("mysmilarstring", "mysimilarstring"), 2);
    assertEquals(75, FuzzySearch.ratio("csr", "c s r"), 2);
  }

  @DisplayName("Test Partial Ratio")
  @Test
  void testPartialRatio() {
    assertEquals(71, FuzzySearch.partialRatio("similar", "somewhresimlrbetweenthisstring"));
    assertEquals(43, FuzzySearch.partialRatio("similar", "notinheresim"));
    assertEquals(38, FuzzySearch.partialRatio("pros holdings, inc.", "settlement facility dow corning trust"));
    assertEquals(33, FuzzySearch.partialRatio("Should be the same", "Opposite ways go alike"));
    assertEquals(33, FuzzySearch.partialRatio("Opposite ways go alike", "Should be the same"));
    assertEquals(58, FuzzySearch.partialRatio("worm_mikeala", "mikeala rath"));
    assertEquals(80, FuzzySearch.partialRatio("c_wasyluka", "crystal wasyluka"));
  }

  @DisplayName("Test Token Sort Partial Ratio")
  @Test
  void testTokenSortPartial() {
    assertEquals(67, FuzzySearch.tokenSortPartialRatio("mvn", "wwwwww.mavencentral.comm"));
    assertEquals(100, FuzzySearch.tokenSortPartialRatio("  order words out of ", "  words out of order"));
    assertEquals(44, FuzzySearch.tokenSortPartialRatio("Testing token set ratio token", "Added another test"));
  }

  @DisplayName("Test Token Sort Ratio")
  @Test
  void testTokenSortRatio() {
    assertEquals(84, FuzzySearch.tokenSortRatio("fuzzy was a bear", "fuzzy fuzzy was a bear"));
  }

  @DisplayName("Test Token Set Ratio")
  @Test
  void testTokenSetRatio() {
    assertEquals(100, FuzzySearch.tokenSetRatio("fuzzy fuzzy fuzzy bear", "fuzzy was a bear"));
    assertEquals(39, FuzzySearch.tokenSetRatio("Testing token set ratio token", "Added another test"));
  }

  @DisplayName("Test Token Set Partial Ratio")
  @Test
  void testTokenSetPartial() {
    assertEquals(11, FuzzySearch.tokenSetPartialRatio("fuzzy was a bear", "blind 100"));
    assertEquals(58, FuzzySearch.tokenSetPartialRatio("worm_mikeala", "mikeala rath"));
    assertEquals(80, FuzzySearch.tokenSetPartialRatio("c_wasyluka", "crystal wasyluka"));
    assertEquals(78, FuzzySearch.tokenSetPartialRatio("a_bacdefg", "crystal bacdefg"));
    assertEquals(67, FuzzySearch.tokenSetPartialRatio("chicago transit authority", "cta"));
  }

  @DisplayName("Test Weighted Ratio")
  @Test
  void testWeightedRatio() {
    assertEquals(60, FuzzySearch.weightedRatio("mvn", "wwwwww.mavencentral.comm"));
    assertEquals(40, FuzzySearch.weightedRatio("mvn", "www;'l3;4;.4;23.4/23.4/234//////www.mavencentral.comm"));
    assertEquals(97, FuzzySearch.weightedRatio("The quick brown fox jimps ofver the small lazy dog",
      "the quick brown fox jumps over the small lazy dog"));
  }

  @DisplayName("Test Extract Top")
  @Test
  void testExtractTop() {
    var res = FuzzySearch.extractTop("goolge", choices, 2);
    var res2 = FuzzySearch.extractTop("goolge", choices, new SimpleRatio(), 2);

    assertEquals(2, res.size());
    assertEquals("google", res.get(0).getString());
    assertEquals("googleplus", res.get(1).getString());

    assertEquals(2, res2.size());
    assertEquals("google", res2.get(0).getString());
    assertEquals("googleplus", res2.get(1).getString());

    assertEquals(0, FuzzySearch.extractTop("goolge", choices, 2, 100).size());
  }

  @DisplayName("Test Extract All")
  @Test
  void testExtractAll() {
    var res = FuzzySearch.extractAll("goolge", choices);

    assertEquals(choices.size(), res.size());
    assertEquals("google", res.get(0).getString());

    assertEquals(3, FuzzySearch.extractAll("goolge", choices, 40).size());
  }

  @DisplayName("Test Extract Sorted")
  @Test
  void testExtractSorted() {
    var res = FuzzySearch.extractSorted("goolge", choices);

    assertEquals(choices.size(), res.size());
    assertEquals("google", res.get(0).getString());
    assertEquals("googleplus", res.get(1).getString());

    assertEquals(3, FuzzySearch.extractSorted("goolge", choices, 40).size());
  }

  @DisplayName("Test Extract One")
  @Test
  void testExtractOne() {
    var res = FuzzySearch.extractOne("twiter", choices, new SimpleRatio());
    var res2 = FuzzySearch.extractOne("twiter", choices);
    var res3 = FuzzySearch.extractOne("cowboys", moreChoices);

    assertEquals("twitter", res.getString());
    assertEquals("twitter", res2.getString());
    assertEquals("Dallas Cowboys", res3.getString());
    assertEquals(90, res3.getScore());
  }

}
