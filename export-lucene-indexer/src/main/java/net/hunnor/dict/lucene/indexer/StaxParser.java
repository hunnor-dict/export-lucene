package net.hunnor.dict.lucene.indexer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import net.hunnor.dict.lucene.model.Entry;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaxParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(StaxParser.class);

  public static final String ENTRY = "entry";

  public static final String FORM_GRP = "formGrp";

  public static final String FORM = "form";

  public static final String ORTH = "orth";

  public static final String POS = "pos";

  public static final String INFL_CODE = "inflCode";

  public static final String INFL_PAR = "inflPar";

  public static final String INFL_SEQ = "inflSeq";

  public static final String SENSE_GRP = "senseGrp";

  public static final String SENSE = "sense";

  public static final String LBL = "lbl";

  public static final String EG = "eg";

  public static final String Q = "q";

  public static final String TRANS = "trans";

  private FileInputStream stream;

  private XMLStreamReader2 reader;

  private static Map<String, String> glues;

  private static Set<String> textNodes;

  static {
    glues = new HashMap<>();
    glues.put(TRANS + "2" + LBL, ", ");
    glues.put(LBL + "2" + LBL, " ");
    glues.put(EG + "2" + LBL, "; ");
    glues.put(TRANS + "2" + EG, "; ");
    glues.put(EG + "2" + EG, "; ");
    glues.put(LBL + "2" + EG, " ");
    glues.put(TRANS + "2" + TRANS, ", ");
    glues.put(LBL + "2" + TRANS, " ");
    glues.put(EG + "2" + TRANS, "; ");

    textNodes = new HashSet<>();
    textNodes.add(ORTH);
    textNodes.add(POS);
    textNodes.add(INFL_SEQ);
    textNodes.add(TRANS);
    textNodes.add(LBL);
    textNodes.add(Q);
  }

  public void setStream(FileInputStream stream) {
    this.stream = stream;
  }

  public void setReader(XMLStreamReader2 reader) {
    this.reader = reader;
  }

  /**
   * Open a stream from an XML file.
   *
   * @param file the file to open
   */
  public void openFile(String file) {
    try {
      if (stream == null) {
        stream = new FileInputStream(file);
      }
      if (reader == null) {
        XMLInputFactory2 xmlInputFactory2 = (XMLInputFactory2) XMLInputFactory2.newInstance();
        xmlInputFactory2.setProperty(XMLInputFactory2.SUPPORT_DTD, false);
        xmlInputFactory2.setProperty(XMLInputFactory2.IS_NAMESPACE_AWARE, false);
        reader = (XMLStreamReader2) xmlInputFactory2.createXMLStreamReader(stream);
      }
    } catch (IOException | XMLStreamException ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
  }

  /**
   * Close the stream of the XML file.
   *
   * @throws XMLStreamException if an error occurs while reading from the stream
   * @throws IOException if an error occurs while reading from the stream
   */
  public void closeFile() {
    try {
      reader.close();
      stream.close();
    } catch (IOException | XMLStreamException ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
  }

  public boolean hasNext() throws XMLStreamException {
    return reader != null && reader.hasNext();
  }

  /**
   * Returns the next entry from the stream, or null if the stream end before an entry can be
   * constructed.
   *
   * @return an Entry with data from the stream, or null
   * @throws XMLStreamException if an error occurs while reading from the stream
   */
  public Entry next() throws XMLStreamException {
    ParserState parserState = new ParserState();
    while (reader.hasNext() && !parserState.isEntryComplete()) {
      int eventType = reader.next();
      switch (eventType) {
        case XMLEvent.START_ELEMENT:
          processStartElement(reader.getLocalName(), parserState);
          break;
        case XMLEvent.CHARACTERS:
          if (parserState.isCollectText()) {
            parserState.getCharacters().append(reader.getText());
          }
          break;
        case XMLEvent.END_ELEMENT:
          processEndElement(reader.getLocalName(), parserState);
          break;
        default:
          break;
      }
    }
    return parserState.getEntry();
  }

  private void processStartElement(String element, ParserState parserState) {
    processEntryStart(element, parserState);
    processFormGrpStart(element, parserState);
    processFormStart(element, parserState);
    processInflCodeStart(element, parserState);
    processInflParStart(element, parserState);
    processSenseGrpStart(element, parserState);
    processSenseStart(element, parserState);
    processEgStart(element, parserState);
    processStart(element, parserState);
  }

  private void processEndElement(String element, ParserState parserState) {
    processEntryEnd(element, parserState);
    processFormEnd(element, parserState);
    processOrthEnd(element, parserState);
    processPosEnd(element, parserState);
    processInflCodeEnd(element, parserState);
    processInflParEnd(element, parserState);
    processInflSeqEnd(element, parserState);
    processSenseGrpEnd(element, parserState);
    processSenseEnd(element, parserState);
    processTransEnd(element, parserState);
    processLblEnd(element, parserState);
    processEgEnd(element, parserState);
    processQEnd(element, parserState);
    processEnd(element, parserState);
  }

  private void processEntryStart(String element, ParserState parserState) {
    if (!ENTRY.equals(element)) {
      return;
    }
    Entry entry = new Entry();
    entry.setId(reader.getAttributeValue(null, "id"));
    parserState.setEntry(entry);
    parserState.setText(new StringBuilder());
    parserState.setSenseGrpBuilder(new StringBuilder());
  }

  private void processFormGrpStart(String element, ParserState parserState) {
    if (!FORM_GRP.equals(element)) {
      return;
    }
    parserState.setFormBuffer(new StringBuilder());
  }

  private void processFormStart(String element, ParserState parserState) {
    if (!FORM.equals(element)) {
      return;
    }
    parserState.setRegularInflection(false);
    parserState.setInflParBuffer(new StringBuilder());
    parserState.setPrimaryForm("yes".equals(reader.getAttributeValue(null, "primary")));
  }

  private void processInflCodeStart(String element, ParserState parserState) {
    if (!INFL_CODE.equals(element)) {
      return;
    }
    if ("suff".equals(reader.getAttributeValue(null, "type"))) {
      parserState.setCollectText(true);
      parserState.setRegularInflection(true);
    }
  }

  private void processInflParStart(String element, ParserState parserState) {
    if (!INFL_PAR.equals(element)) {
      return;
    }
    if (parserState.getInflParBuffer().length() > 0) {
      parserState.getInflParBuffer().append("; ");
      parserState.setInflSeqBuffer(new StringBuilder());
    }
  }

  private void processSenseGrpStart(String element, ParserState parserState) {
    if (!SENSE_GRP.equals(element)) {
      return;
    }
    parserState.setSenseBuffer(new StringBuilder());
    int senseGrpCount = parserState.getSenseGrpCount();
    if (senseGrpCount > 0) {
      if (senseGrpCount == 1) {
        parserState.getSenseGrpBuffer().insert(0, "<b>I</b> ");
      }
      parserState
          .getSenseGrpBuffer()
          .append(" <b>" + RomanNumerals.roman(senseGrpCount + 1) + "</b> ");
    }
  }

  private void processSenseStart(String element, ParserState parserState) {
    if (!SENSE.equals(element)) {
      return;
    }
    if (parserState.getSenseCount() > 0) {
      if (parserState.getSenseCount() == 1) {
        parserState.getSenseBuffer().insert(0, "<b>1</b> ");
      }
      parserState.getSenseBuffer().append(" <b>" + (parserState.getSenseCount() + 1) + "</b> ");
    }
  }

  private void processEgStart(String element, ParserState parserState) {
    if (!EG.equals(element)) {
      return;
    }
    parserState.setEgBuffer(new StringBuilder());
    parserState.setEg(true);
  }

  private void processStart(String element, ParserState parserState) {
    if (isTextNode(element)) {
      parserState.setCollectText(true);
    }
  }

  private void processEntryEnd(String element, ParserState parserState) {
    if (!ENTRY.equals(element)) {
      return;
    }
    parserState
        .getText()
        .append(parserState.getFormBuffer())
        .append(" ")
        .append(parserState.getSenseGrpBuffer());
    parserState.getEntry().setText(parserState.getText().toString());
    parserState.setEntryComplete(true);
  }

  private void processFormEnd(String element, ParserState parserState) {
    if (!FORM.equals(element)) {
      return;
    }
    if (!parserState.isRegularInflection() && parserState.getInflParBuffer().length() > 0) {
      parserState.getFormBuffer().append(" (" + parserState.getInflParBuffer() + ")");
    }
  }

  private void processOrthEnd(String element, ParserState parserState) {
    if (!ORTH.equals(element)) {
      return;
    }
    parserState.getEntry().getRoots().add(parserState.getCharacters().toString());
    if (parserState.getEntry().getSort() == null) {
      parserState.getEntry().setSort(parserState.getCharacters().toString());
    }
    if (parserState.getFormBuffer().length() > 0) {
      parserState.getFormBuffer().append(" ");
    }
    parserState.getFormBuffer().append("<b>" + parserState.getCharacters().toString() + "</b>");
    parserState.setCollectText(false);
  }

  private void processPosEnd(String element, ParserState parserState) {
    if (!POS.equals(element)) {
      return;
    }
    if (parserState.isPrimaryForm()) {
      parserState.getFormBuffer().append(" " + parserState.getCharacters().toString());
    }
    parserState.setCollectText(false);
  }

  private void processInflCodeEnd(String element, ParserState parserState) {
    if (!INFL_CODE.equals(element)) {
      return;
    }
    if (parserState.getCharacters().length() > 0) {
      parserState.getFormBuffer().append(" " + parserState.getCharacters().toString());
    }
    parserState.setCollectText(false);
  }

  private void processInflParEnd(String element, ParserState parserState) {
    if (!INFL_PAR.equals(element)) {
      return;
    }
    if (parserState.getInflParBuffer().length() > 0) {
      parserState.getInflParBuffer().append("; ");
    }
    parserState.getInflParBuffer().append(parserState.getInflSeqBuffer());
    parserState.setCollectText(false);
  }

  private void processInflSeqEnd(String element, ParserState parserState) {
    if (!INFL_SEQ.equals(element)) {
      return;
    }
    parserState.getEntry().getForms().add(parserState.getCharacters().toString());
    if (parserState.getInflSeqBuffer().length() > 0) {
      parserState.getInflSeqBuffer().append(", ");
    }
    parserState.getInflSeqBuffer().append(parserState.getCharacters().toString());
    parserState.setCollectText(false);
  }

  private void processSenseGrpEnd(String element, ParserState parserState) {
    if (!SENSE_GRP.equals(element)) {
      return;
    }
    parserState.getSenseGrpBuffer().append(parserState.getSenseBuffer());
    parserState.setSenseGrpCount(parserState.getSenseGrpCount() + 1);
    parserState.setSenseCount(0);
  }

  private void processSenseEnd(String element, ParserState parserState) {
    if (!SENSE.equals(element)) {
      return;
    }
    parserState.setSenseCount(parserState.getSenseCount() + 1);
  }

  private void processTransEnd(String element, ParserState parserState) {
    if (!TRANS.equals(element)) {
      return;
    }
    if (parserState.isEg()) {
      parserState.getEntry().getQuoteTrans().add(parserState.getCharacters().toString());
      if (TRANS.equals(parserState.getPrevious())) {
        parserState.getEgBuffer().append(", ");
      } else if (LBL.equals(parserState.getPrevious())
          || Q.equals(parserState.getPrevious())) {
        parserState.getEgBuffer().append(" ");
      }
      parserState.getEgBuffer().append(parserState.getCharacters().toString());
    } else {
      parserState.getEntry().getTrans().add(parserState.getCharacters().toString());
      parserState.getSenseBuffer().append(getGlue(element, parserState.getPrevious()));
      parserState.getSenseBuffer().append(parserState.getCharacters().toString());
    }
    parserState.setCollectText(false);
  }

  private void processLblEnd(String element, ParserState parserState) {
    if (!LBL.equals(element)) {
      return;
    }
    if (parserState.getSenseBuffer().length() > 0) {
      parserState.getSenseBuffer().append(getGlue(element, parserState.getPrevious()));
    }
    parserState.getSenseBuffer().append("<i>" + parserState.getCharacters().toString() + "</i>");
    parserState.setCollectText(false);
  }

  private void processEgEnd(String element, ParserState parserState) {
    if (!EG.equals(element)) {
      return;
    }
    parserState.setEg(false);
    if (parserState.getSenseBuffer().length() > 0) {
      parserState.getSenseBuffer().append(getGlue(element, parserState.getPrevious()));
    }
    parserState.getSenseBuffer().append(parserState.getEgBuffer());
  }

  private void processQEnd(String element, ParserState parserState) {
    if (!Q.equals(element)) {
      return;
    }
    parserState.getEntry().getQuote().add(parserState.getCharacters().toString());
    parserState.setCollectText(false);
    if (parserState.getEgBuffer().length() > 0) {
      parserState.getEgBuffer().append(", ");
    }
    parserState.getEgBuffer().append("<b>" + parserState.getCharacters().toString() + "</b>");
  }

  private void processEnd(String element, ParserState parserState) {
    parserState.setPrevious(element);
    parserState.setCharacters(new StringBuilder());
  }

  private String getGlue(String element, String previous) {
    String glue = glues.get(previous + "2" + element);
    if (glue == null) {
      glue = "";
    }
    return glue;
  }

  private boolean isTextNode(String elementName) {
    return textNodes.contains(elementName);
  }

}
