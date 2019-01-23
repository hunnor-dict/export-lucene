package net.hunnor.dict.lucene;

import net.hunnor.dict.lucene.indexer.Service;
import net.hunnor.dict.lucene.model.Language;

import org.apache.tools.ant.Task;

public class AntTask extends Task {

  private String language;

  private String main;

  private String source;

  private String spelling;

  public void setLanguage(String language) {
    this.language = language;
  }

  public void setMain(String main) {
    this.main = main;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public void setSpelling(String spelling) {
    this.spelling = spelling;
  }

  /**
   * The method executed by Ant.
   */
  @Override
  public void execute() {

    Service service = new Service();

    service.indexFile(source, Language.valueOf(language), main);
    service.indexSuggestions(main, spelling);

  }

}
