package hw7.search;

import hw7.Map;
import java.io.File;
import java.util.List;
import java.util.Set;
import obfuscate.SimpleSearchEngine;

/**
 * This is a wrapper class around staff's implementation of JHUgle.
 */
public class Jhugle implements SearchEngine {

  // A supremely simple search engine based on hash table.
  private static SimpleSearchEngine sse;

  /**
   * Construct a JHUgle search engine.
   *
   * @param map any implementation of Map.
   */
  public Jhugle(Map<String, Set<String>> map) {
    sse = new SimpleSearchEngine(map);
  }

  /**
   * Creates an association between each keyword and all the website
   * (urls) that contain that keyword.
   *
   * @param data File must be a list of websites and the keywords
   *             that are contained on each site, in the format
   *             specified in homework instructions.
   */
  @Override
  public void buildSearchEngine(File data) {
    sse.buildSearchEngine(data);
  }

  /**
   * Runs a command-line query engine for JHUgle.
   */
  @Override
  public void runSearchEngine() {
    sse.runSearchEngine();
  }

  /**
   * Query the search Engine without running rusSearchEngine.
   * This is useful for testing & development.
   *
   * @param query is a string like "foo bar && blah || ? !"
   *              it must include at least one ? and ! (ideally end with !).
   * @return list of URLs that would have been printed to the screen
   *         as a result of executing the query.
   */
  @Override
  public List<String> queryWithoutRunning(String query) {
    // Pre-condition: query is valid.
    return sse.queryWithoutRunning(query);
  }
}

