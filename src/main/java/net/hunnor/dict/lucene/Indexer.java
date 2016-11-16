package net.hunnor.dict.lucene;

import java.io.IOException;
import java.util.List;

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
					if ("purge".equals(args[1])) {
						purgeIndex();
					} else if ("file".equals(args[1])) {
						if (args.length < 4) {
							printUsage();
						} else {
							indexFile(args[2], args[3]);
						}
					} else if ("dump".equals(args[1])) {
						dumpIndex();
					} else {
						printUsage();
					}
				}
			} else if ("search".equals(args[0])) {
				search();
			} else if ("spellcheck".equals(args[0])) {
				if (args.length < 2) {
					printUsage();
				} else {
					if ("index".equals(args[1])) {
						indexSuggestions();
					} else if ("suggest".equals(args[1])) {
						if (args.length < 3) {
							printUsage();
						} else {
							suggest(args[2]);
						}
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

	private static void purgeIndex() {
		try {
			DictionaryParser dictionaryParser = new DictionaryParser();
			dictionaryParser.openIndexWriter(INDEX_DIR);
			dictionaryParser.deleteAll();
			dictionaryParser.closeIndexWriter();
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public static void dumpIndex() {
		try {
			DictionaryParser dictionaryParser = new DictionaryParser();
			dictionaryParser.openIndexReader(INDEX_DIR);
			int numDocs = dictionaryParser.numDocs();
			for (int i = 0; i < numDocs; i++) {
				IndexObject indexObject = dictionaryParser.read(i);
				System.out.println(indexObject.getId() + "|" + indexObject.getLang() + "|" + indexObject.getText());
			}
			dictionaryParser.closeIndexReader();
		} catch(IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public static void search() {
		try {
			String query = "hu_roots:keves";
			DictionaryParser dictionaryParser = new DictionaryParser();
			dictionaryParser.openIndexReader(INDEX_DIR);
			List<IndexObject> results = dictionaryParser.search(query);
			if (results != null) {
				for (IndexObject result: results) {
					System.out.println(result.getId() + "|" + result.getLang() + "|" + result.getText());
				}
			} else {
				System.out.println("ERROR: Result list NULL.");
			}
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public static void suggest(String term) {
		try {
			DictionaryParser dictionaryParser = new DictionaryParser();
			dictionaryParser.openSpellChecker(SPELLING_DIR);
			System.out.println("Suggestions for '" + term + "':");
			String[] suggestions = dictionaryParser.suggest(term);
			for (String suggestion: suggestions) {
				System.out.println("-> " + suggestion);
			}
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public static void printUsage() {
		System.out.println("Usage:");
		System.out.println("-> java -jar hunnor-indexer-android.jar index file 'file' 'lang': Index 'file' as 'lang'");
		System.out.println("-> java -jar hunnor-indexer-android.jar index purge: Delete all documents");
		System.out.println("-> java -jar hunnor-indexer-android.jar index dump: Dump stored fields to standard output");
		System.out.println("");
		System.out.println("-> java -jar hunnor-indexer-android.jar spellcheck index: Create spellchecking index");
		System.out.println("-> java -jar hunnor-indexer-android.jar spellcheck suggest 'foo': Get suggestions for 'foo'");
		System.out.println("");
	}

}
