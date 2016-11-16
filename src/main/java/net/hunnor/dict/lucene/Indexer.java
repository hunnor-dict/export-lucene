package net.hunnor.dict.lucene;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

public class Indexer {

	private static String INDEX_DIR = "hunnor-lucene-index";
	private static String SPELLING_DIR = "hunnor-lucene-spelling";

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			printUsage();
		} else {
			if ("index".equals(args[0])) {
				if (args.length < 2) {
					printUsage();
				} else {
					if ("file".equals(args[1])) {
						if (args.length < 4) {
							printUsage();
						} else {
							indexFile(args[2], args[3]);
						}
					} else {
						printUsage();
					}
				}
			} else if ("spellcheck".equals(args[0])) {
				if (args.length < 2) {
					printUsage();
				} else {
					if ("index".equals(args[1])) {
						indexSuggestions();
					} else {
						printUsage();
					}
				}
			} else {
				printUsage();
			}
		}
	}

	private static void indexFile(String file, String lang) {
		try {
			DictionaryParser dictionaryParser = new DictionaryParser();
			dictionaryParser.openIndexWriter(INDEX_DIR);
			dictionaryParser.parseFile(file, lang);
			dictionaryParser.closeIndexWriter();
		} catch (XMLStreamException e) {
			System.out.println("Error: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	private static void indexSuggestions() {
		try {
			DictionaryParser dictionaryParser = new DictionaryParser();
			dictionaryParser.openIndexReader(INDEX_DIR);
			dictionaryParser.openSpellChecker(SPELLING_DIR);
			dictionaryParser.createSuggestions();
			dictionaryParser.closeSpellChecker();
			dictionaryParser.closeIndexReader();
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}		
	}

	public static void printUsage() {
		System.out.println("Usage:");
		System.out.println("-> java -jar hunnor-indexer-android.jar index file 'file' 'lang': Index 'file' as 'lang'");
		System.out.println("");
		System.out.println("-> java -jar hunnor-indexer-android.jar spellcheck index: Create spellchecking index");
		System.out.println("");
	}

}
