package net.hunnor.dict.lucene;

import net.hunnor.dict.lucene.model.Language;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Launcher {

  private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

  private static final String OPTS_XML = "x";

  private static final String OPTS_LANG = "l";

  private static final String OPTS_SPELLCHECK_INDEX_DIR = "s";

  private static final String OPTS_INDEX_DIR = "d";

  private Launcher() {
  }

  /**
   * The main method of the application.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {

    Options options = new Options();

    Option language = Option.builder(OPTS_LANG)
        .desc("the language to index the file as ('hu' or 'no')")
        .hasArg().required().build();
    options.addOption(language);

    Option xml = Option.builder(OPTS_XML)
        .desc("the XML file to parse")
        .hasArg().required().build();
    options.addOption(xml);

    Option indexDir = Option.builder(OPTS_INDEX_DIR)
        .desc("the directory for the main index")
        .hasArg().required().build();
    options.addOption(indexDir);

    Option spellingDir = Option.builder(OPTS_SPELLCHECK_INDEX_DIR)
        .desc("the directory for the spell checking index")
        .hasArg().required().build();
    options.addOption(spellingDir);

    CommandLineParser parser = new DefaultParser();
    CommandLine commandLine = null;

    Language lang = null;
    try {

      commandLine = parser.parse(options, args);

      lang = Language.valueOf(commandLine.getOptionValue(OPTS_LANG));

    } catch (ParseException | IllegalArgumentException ex) {

      LOGGER.error(ex.getMessage(), ex);

      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar export-lucene.jar", options);

      System.exit(1);

    }


    Service service = new Service();
    service.indexFile(
        commandLine.getOptionValue(OPTS_XML),
        lang,
        commandLine.getOptionValue(OPTS_INDEX_DIR));
    service.indexSuggestions(
        commandLine.getOptionValue(OPTS_INDEX_DIR),
        commandLine.getOptionValue(OPTS_SPELLCHECK_INDEX_DIR));

  }

}
