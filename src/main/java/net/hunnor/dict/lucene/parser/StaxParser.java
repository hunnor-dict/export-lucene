package net.hunnor.dict.lucene.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;

import net.hunnor.dict.lucene.model.Entry;
import net.hunnor.dict.lucene.util.RomanNumerals;

/**
 * Parses an XML file and returns model objects.
 */
public class StaxParser {

	/**
	 * The default namespace of the XML export.
	 */
	private static final String XML_NS = "http://dict.hunnor.net";

	/**
	 * Reader for the XML stream.
	 */
	private XMLStreamReader2 reader;

	/**
	 * Open a stream from an XML file.
	 * @param file the file to open
	 * @param lang the language to index the file as
	 * @throws XMLStreamException 
	 * @throws FileNotFoundException 
	 */
	public void openFile(final String file, final String lang)
			throws FileNotFoundException, XMLStreamException {
		XMLInputFactory2 xmlInputFactory2 =
				(XMLInputFactory2) XMLInputFactory2.newInstance();
		reader = (XMLStreamReader2) xmlInputFactory2
				.createXMLStreamReader(file, new FileInputStream(file));
	}

	/**
	 * Check if the parser has reached the end of the stream.
	 * The method returns true if there are events left in the
	 * stream. The events left might not contain any entries.
	 * @return true if the XML stream has events left, false otherwise
	 * @throws XMLStreamException 
	 */
	public boolean hasNext() throws XMLStreamException {
		return reader != null && reader.hasNext();
	}

	/**
	 * Returns the next entry from the stream, or null if the stream
	 * end before an entry can be constructed.
	 * @return an Entry with data from the stream, or null
	 * @throws XMLStreamException 
	 */
	public Entry next() throws XMLStreamException {
		Entry entry = new Entry();
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

		while (reader.hasNext()) {
			eventType = reader.next();
			switch (eventType) {
			case XMLEvent.START_ELEMENT:
				element = reader.getName().toString();
				if (("{" + XML_NS + "}entry").equals(element)) {
					entry = new Entry();
					entry.setId(reader
							.getAttributeValue(null, "id"));
					text = "";
					senseGrpBuffer = "";
				} else if (("{" + XML_NS + "}formGrp").equals(element)) {
					formBuffer = "";
				} else if (("{" + XML_NS + "}form").equals(element)) {
					regularInflection = false;
					inflParBuffer = "";
					if ("yes".equals(reader
							.getAttributeValue(null, "primary"))) {
						primaryForm = true;
					} else {
						primaryForm = false;
					}
				} else if (("{" + XML_NS + "}orth").equals(element)) {
					getText = true;
				} else if (("{" + XML_NS + "}pos").equals(element)) {
					getText = true;
				} else if (("{" + XML_NS + "}inflCode").equals(element)) {
					if ("suff".equals(reader
							.getAttributeValue(null, "type"))) {
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
						senseGrpBuffer = senseGrpBuffer
								+ " <b>"
								+ RomanNumerals.roman(senseGrpCount + 1)
								+ "</b> ";
					}
				} else if (("{" + XML_NS + "}sense").equals(element)) {
					if (senseCount > 0) {
						if (senseCount == 1) {
							senseBuffer = "<b>1</b> " + senseBuffer;
						}
						senseBuffer = senseBuffer
								+ " <b>" + (senseCount + 1) + "</b> ";
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
					characters.append(reader.getText());
				}
			break;
			case XMLEvent.END_ELEMENT:
				element = reader.getName().toString();
				if (("{" + XML_NS + "}entry").equals(element)) {
					text = text + formBuffer + " " + senseGrpBuffer;
					entry.setText(text);
					return entry;
				} else if (("{" + XML_NS + "}form").equals(element)) {
					if (!regularInflection) {
						if (inflParBuffer.length() > 0) {
							formBuffer = formBuffer
									+ " (" + inflParBuffer + ")";
						}
					}
				} else if (("{" + XML_NS + "}orth").equals(element)) {
					entry.getRoots().add(characters.toString());
					if (formBuffer.length() > 0) {
						formBuffer = formBuffer + " ";
					}
					formBuffer = formBuffer
							+ "<b>" + characters.toString() + "</b>";
					getText = false;
				} else if (("{" + XML_NS + "}pos").equals(element)) {
					if (primaryForm) {
						formBuffer = formBuffer + " " + characters.toString();
					}
					getText = false;
				} else if (("{" + XML_NS + "}inflCode").equals(element)) {
					if (characters.length() > 0) {
						formBuffer = formBuffer + " " + characters.toString();
					}
					getText = false;
				} else if (("{" + XML_NS + "}inflPar").equals(element)) {
					if (inflParBuffer.length() > 0) {
						inflParBuffer = inflParBuffer + "; ";
					}
					inflParBuffer = inflParBuffer + inflSeqBuffer;
					getText = false;
				} else if (("{" + XML_NS + "}inflSeq").equals(element)) {
						entry.getForms().add(characters.toString());
					if (inflSeqBuffer.length() > 0) {
						inflSeqBuffer = inflSeqBuffer + ", ";
					}
					inflSeqBuffer = inflSeqBuffer + characters.toString();
					getText = false;
				} else if (("{" + XML_NS + "}senseGrp").equals(element)) {
					senseGrpBuffer = senseGrpBuffer + senseBuffer;
					senseGrpCount++;
					senseCount = 0;
				} else if (("{" + XML_NS + "}sense").equals(element)) {
					previous = "";
					senseCount++;
				} else if (("{" + XML_NS + "}trans").equals(element)) {
					if (isEg) {
						entry.getQuoteTrans().add(characters.toString());
						if ("trans".equals(previous)) {
							egBuffer = egBuffer + ", ";
						} else if ("lbl".equals(previous)) {
							egBuffer = egBuffer + " ";
						} else if ("q".equals(previous)) {
							egBuffer = egBuffer + " ";
						}
						egBuffer = egBuffer + characters.toString();
					} else {
						entry.getTrans().add(characters.toString());
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
					senseBuffer = senseBuffer
							+ "<i>" + characters.toString() + "</i>";
					previous = "lbl";
					getText = false;
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
				} else if (("{" + XML_NS + "}q").equals(element)) {
					entry.getQuote().add(characters.toString());
					getText = false;
					if (egBuffer.length() > 0) {
						egBuffer = egBuffer + ", ";
					}
					egBuffer = egBuffer
							+ "<b>" + characters.toString() + "</b>";
					previous = "q";
				}
				characters = new StringBuilder();
			break;
			default:
			break;
			}
		}
		return null;
	}

}
