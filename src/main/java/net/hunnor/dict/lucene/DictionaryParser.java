package net.hunnor.dict.lucene;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

public class DictionaryParser {

	private final String XML_NS = "http://dict.hunnor.net";

	private IndexHandler indexHandler;

	private IndexObject indexObject;

	public void openIndexReader(String indexDir) throws IOException {
		if (indexHandler == null) {
			indexHandler = new IndexHandler();
		}
		indexHandler.setIndexDir(indexDir);
		indexHandler.openIndexReader();
	}

	public void openIndexWriter(String indexDir) throws IOException {
		if (indexHandler == null) {
			indexHandler = new IndexHandler();
		}
		indexHandler.setIndexDir(indexDir);
		indexHandler.openIndexWriter();
	}

	public void openSpellChecker(String spellingDir) throws IOException {
		if (indexHandler == null) {
			indexHandler = new IndexHandler();
		}
		indexHandler.setSpellingDir(spellingDir);
		indexHandler.openSpellChecker();
	}

	public void closeIndexReader() throws IOException {
		if (indexHandler != null) {
			indexHandler.closeIndexReader();
		}
	}

	public void closeIndexWriter() throws IOException {
		if (indexHandler != null) {
			indexHandler.closeIndexWriter();
		}
	}

	public void closeSpellChecker() throws IOException {
		if (indexHandler != null) {
			indexHandler.closeSpellChecker();
		}
	}

	public void deleteAll() throws IOException {
		indexHandler.deleteAll();
	}

	public int numDocs() {
		return indexHandler.numDocs();
	}

	public IndexObject read(int id) throws IOException {
		 return indexHandler.read(id);
	}

	public List<IndexObject> search(String query) {
		return indexHandler.search(query);
	}

	public void createSuggestions() throws IOException {
		indexHandler.createSuggestions();
	}

	public String[] suggest(String term) throws IOException {
		return indexHandler.suggest(term);
	}

	public void parseFile(String file, String lang) throws XMLStreamException, IOException {
		XMLInputFactory2 xmlInputFactory2 = (XMLInputFactory2) XMLInputFactory2.newInstance();
		XMLStreamReader2 xmlStreamReader2 = (XMLStreamReader2)xmlInputFactory2.createXMLStreamReader(file, new FileInputStream(file));

		int eventType = 0;
		String element = "";
		StringBuilder characters = new StringBuilder();
		boolean getText = false;
		boolean isEg = false;
		boolean regularInflection = false;
		boolean primaryForm = false;
		String previous = "";
		String text = "";
		String formBuffer = "";
		String inflParBuffer = "";
		String inflSeqBuffer = "";
		int senseGrpCount = 0;
		String senseGrpBuffer = "";
		int senseCount = 0;
		String senseBuffer = "";
		String egBuffer = "";

		while(xmlStreamReader2.hasNext()) {
			eventType = xmlStreamReader2.next();
			switch (eventType) {
			case XMLEvent.START_ELEMENT:
				element = xmlStreamReader2.getName().toString();
				if (("{" + XML_NS + "}entry").equals(element)) {
					indexObject = new IndexObject();
					indexObject.setLang(lang);
					indexObject.setId(xmlStreamReader2.getAttributeValue(null, "id"));
					text = "";
					senseGrpBuffer = "";
				} else if (("{" + XML_NS + "}formGrp").equals(element)) {
					formBuffer = "";
				} else if (("{" + XML_NS + "}form").equals(element)) {
					regularInflection = false;
					inflParBuffer = "";
					if ("yes".equals(xmlStreamReader2.getAttributeValue(null, "primary"))) {
						primaryForm = true;
					} else {
						primaryForm = false;
					}
				} else if (("{" + XML_NS + "}orth").equals(element)) {
					getText = true;
				} else if (("{" + XML_NS + "}pos").equals(element)) {
					getText = true;
				} else if (("{" + XML_NS + "}inflCode").equals(element)) {
					if ("suff".equals(xmlStreamReader2.getAttributeValue(null, "type"))) {
						getText = true;
						regularInflection = true;
					}
				} else if (("{" + XML_NS + "}inflPar").equals(element)) {
					if (inflParBuffer.length() > 0) {
						inflParBuffer = inflParBuffer + "; ";
					}
					inflSeqBuffer = "";
				} else if (("{" + XML_NS + "}inflSeq").equals(element)) {
					getText = true;
				} else if (("{" + XML_NS + "}senseGrp").equals(element)) {
					senseBuffer = "";
					if (senseGrpCount > 0) {
						if (senseGrpCount == 1) {
							senseGrpBuffer = "<b>I</b> " + senseGrpBuffer;
						}
						senseGrpBuffer = senseGrpBuffer + " <b>" + RomanNumerals.roman(senseGrpCount + 1) + "</b> ";
					}
				} else if (("{" + XML_NS + "}sense").equals(element)) {
					if (senseCount > 0) {
						if (senseCount == 1) {
							senseBuffer = "<b>1</b> " + senseBuffer;
						}
						senseBuffer = senseBuffer + " <b>" + (senseCount + 1) + "</b> ";
					}
				} else if (("{" + XML_NS + "}trans").equals(element)) {
					getText = true;
				} else if (("{" + XML_NS + "}lbl").equals(element)) {
					getText = true;
				} else if (("{" + XML_NS + "}eg").equals(element)) {
					egBuffer = "";
					isEg = true;
				} else if (("{" + XML_NS + "}q").equals(element)) {
					getText = true;
				}
			break;
			case XMLEvent.CHARACTERS:
				if (getText) {
					characters.append(xmlStreamReader2.getText());
				}
			break;
			case XMLEvent.END_ELEMENT:
				element = xmlStreamReader2.getName().toString();
				// <entry>
				if (("{" + XML_NS + "}entry").equals(element)) {
					text = text + formBuffer + " " + senseGrpBuffer;
					indexObject.setText(text);
					indexHandler.write(indexObject);
					senseGrpCount = 0;
				// <orth>
				} else if (("{" + XML_NS + "}form").equals(element)) {
					if (!regularInflection) {
						if (inflParBuffer.length() > 0) {
							formBuffer = formBuffer + " (" + inflParBuffer + ")";
						}
					}
				} else if (("{" + XML_NS + "}orth").equals(element)) {
					if (indexObject.getRoots() == null) {
						indexObject.setRoots(new ArrayList<String>());
					}
					indexObject.getRoots().add(characters.toString());
					if (formBuffer.length() > 0) {
						formBuffer = formBuffer + " ";
					}
					formBuffer = formBuffer + "<b>" + characters.toString() + "</b>";
					getText = false;
				// <pos>
				} else if (("{" + XML_NS + "}pos").equals(element)) {
					if (primaryForm) {
						formBuffer = formBuffer + " " + characters.toString();
					}
					getText = false;
				// <inflCode>
				} else if (("{" + XML_NS + "}inflCode").equals(element)) {
					if (characters.length() > 0) {
						formBuffer = formBuffer + " " + characters.toString();
					}
					getText = false;
				// <inflPar>
				} else if (("{" + XML_NS + "}inflPar").equals(element)) {
					if (inflParBuffer.length() > 0) {
						inflParBuffer = inflParBuffer + "; ";
					}
					inflParBuffer = inflParBuffer + inflSeqBuffer;
					getText = false;
				// <inflSeq>
				} else if (("{" + XML_NS + "}inflSeq").equals(element)) {
					if (indexObject.getForms() == null) {
						indexObject.setForms(new ArrayList<String>());
					}
					if (!indexObject.getForms().contains(characters.toString())) {
						indexObject.getForms().add(characters.toString());
					}
					if (inflSeqBuffer.length() > 0) {
						inflSeqBuffer = inflSeqBuffer + ", ";
					}
					inflSeqBuffer = inflSeqBuffer + characters.toString();
					getText = false;
				// <senseGrp>
				} else if (("{" + XML_NS + "}senseGrp").equals(element)) {
					senseGrpBuffer = senseGrpBuffer + senseBuffer;
					senseGrpCount++;
					senseCount = 0;
				// <sense>
				} else if (("{" + XML_NS + "}sense").equals(element)) {
					previous = "";
					senseCount++;
				// <trans>
				} else if (("{" + XML_NS + "}trans").equals(element)) {
					if (isEg) {
						if (indexObject.getQuoteTrans() == null) {
							indexObject.setQuoteTrans(new ArrayList<String>());
						}
						indexObject.getQuoteTrans().add(characters.toString());
						if ("trans".equals(previous)) {
							egBuffer = egBuffer + ", ";
						} else if ("lbl".equals(previous)) {
							egBuffer = egBuffer + " ";
						} else if ("q".equals(previous)) {
							egBuffer = egBuffer + " ";
						}
						egBuffer = egBuffer + characters.toString();
					} else {
						if (indexObject.getTrans() == null) {
							indexObject.setTrans(new ArrayList<String>());
						}
						indexObject.getTrans().add(characters.toString());
						if ("trans".equals(previous)) {
							senseBuffer = senseBuffer + ", ";
						} else if ("lbl".equals(previous)) {
							senseBuffer = senseBuffer + " ";
						} else if ("eg".equals(previous)) {
							senseBuffer = senseBuffer + "; ";
						}
						senseBuffer = senseBuffer + characters.toString();
					}
					previous = "trans";
					getText = false;
				// <lbl>
				} else if (("{" + XML_NS + "}lbl").equals(element)) {
					if (senseBuffer.length() > 0) {
						if ("trans".equals(previous)) {
							senseBuffer = senseBuffer + ", ";
						} else if ("lbl".equals(previous)) {
							senseBuffer = senseBuffer + " ";
						} else if ("eg".equals(previous)) {
							senseBuffer = senseBuffer + "; ";
						}
					}
					senseBuffer = senseBuffer + "<i>" + characters.toString() + "</i>";
					previous = "lbl";
					getText = false;
				// <eg>
				} else if (("{" + XML_NS + "}eg").equals(element)) {
					isEg = false;
					previous = "eg";
					if (senseBuffer.length() > 0) {
						if ("trans".equals(previous)) {
							senseBuffer = senseBuffer + "; ";
						} else if ("lbl".equals(previous)) {
							senseBuffer = senseBuffer + " ";
						} else if ("eg".equals(previous)) {
							senseBuffer = senseBuffer + "; ";
						}
					}
					senseBuffer = senseBuffer + egBuffer;
				// <q>
				} else if (("{" + XML_NS + "}q").equals(element)) {
					if (indexObject.getQuote() == null) {
						indexObject.setQuote(new ArrayList<String>());
					}
					indexObject.getQuote().add(characters.toString());
					getText = false;
					if (egBuffer.length() > 0) {
						egBuffer = egBuffer + ", ";
					}
					egBuffer = egBuffer + "<b>" + characters.toString() + "</b>";
					previous = "q";
				}
				characters = new StringBuilder();
			break;
			}
		}
	}

}
