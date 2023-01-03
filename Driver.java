package hw7;

import hw7.search.Jhugle;

public class Driver {

  /**
   * Execution starts here.
   *
   * @param args command-line arguments not used here.
   */
  public static void main(String[] args) {
    Jhugle jhUgle = new Jhugle(Config.getMap());
    jhUgle.buildSearchEngine(Config.getDataFile());
    jhUgle.runSearchEngine();
  }
}
