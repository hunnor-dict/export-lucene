package net.hunnor.dict.lucene;

import net.hunnor.dict.lucene.indexer.LuceneIndexer;
import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.model.Language;
import net.hunnor.dict.lucene.parser.StaxParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;

public class Service {

  private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

  private StaxParser staxParser;

  private LuceneIndexer luceneIndexer;

  /**
   * Create the main index.
   *
   * @param file the file to index
   * @param lang the language to index the file as
   * @param indexDir the directory to create the index in
   */
  public void indexFile(String file, Language lang, String indexDir) {

    try {

      luceneIndexer = getIndexer();
      luceneIndexer.setIndexDir(indexDir);
      luceneIndexer.openIndexWriter();

      staxParser = getParser();
      staxParser.openFile(file);
      while (staxParser.hasNext()) {
        Entry entry = staxParser.next();
        if (entry != null) {
          entry.setLang(lang);
          luceneIndexer.write(entry);
        }
      }
      staxParser.closeFile();

      luceneIndexer.closeIndexWriter();

    } catch (IOException | XMLStreamException ex) {
      LOGGER.error(ex.getMessage(), ex);
    }

  }

  /**
   * Create the spell checking index from the main index.
   *
   * @param indexDir the directory with the main index
   * @param spellcheckDir the directory to create the spell checking index in
   */
  public void indexSuggestions(String indexDir, String spellcheckDir) {

    try {

      luceneIndexer = getIndexer();

      luceneIndexer.setIndexDir(indexDir);
      luceneIndexer.openIndexReader();
      luceneIndexer.setSpellingDir(spellcheckDir);
      luceneIndexer.openSpellChecker();

      luceneIndexer.createSuggestions();

      luceneIndexer.closeSpellChecker();
      luceneIndexer.closeIndexReader();

    } catch (IOException ex) {
      LOGGER.error(ex.getMessage(), ex);
    }

  }

  private StaxParser getParser() {
    return new StaxParser();
  }

  private LuceneIndexer getIndexer() {
    return new LuceneIndexer();
  }

}
