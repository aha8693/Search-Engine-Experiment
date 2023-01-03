package hw7.search;

import java.io.File;
import java.util.List;

public interface SearchEngine {
  /**
   * Creates an association between each keyword and all the website
   * (urls) that contain that keyword.
   *
   * @param data File must be a list of websites and the keywords
   *             that are contained on each site, in the format
   *             specified in homework instructions.
   */
  void buildSearchEngine(File data);

  /**
   * Runs a command-line query engine for JHUgle.
   */
  void runSearchEngine();

  /**
   * Query the search Engine without running rusSearchEngine.
   * This is useful for testing & development.
   *
   * @param query is a string like "foo bar && blah || ? !"
   *              it must include at least one ? and ! (ideally end with !).
   * @return list of URLs that would have been printed to the screen
   *         as a result of executing the query.
   */
  List<String> queryWithoutRunning(String query);
}
