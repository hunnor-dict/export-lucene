package net.hunnor.dict.lucene.parser;

import net.hunnor.dict.lucene.model.Entry;

public final class ParserState {

  private String previous;

  private Entry entry;

  private boolean entryComplete;

  private StringBuilder text = new StringBuilder();

  private boolean collectText;

  private StringBuilder characters = new StringBuilder();

  private StringBuilder formBuffer = new StringBuilder();

  private boolean primaryForm;

  private boolean regularInflection;

  private StringBuilder inflParBuffer = new StringBuilder();

  private StringBuilder inflSeqBuffer = new StringBuilder();

  private StringBuilder senseGrpBuffer = new StringBuilder();

  private int senseGrpCount;

  private StringBuilder senseBuffer = new StringBuilder();

  private int senseCount;

  private boolean eg;

  private StringBuilder egBuffer = new StringBuilder();

  public String getPrevious() {
    return previous;
  }

  public void setPrevious(final String previous) {
    this.previous = previous;
  }

  public Entry getEntry() {
    return entry;
  }

  public void setEntry(final Entry entry) {
    this.entry = entry;
  }

  public boolean isEntryComplete() {
    return entryComplete;
  }

  public void setEntryComplete(final boolean entryComplete) {
    this.entryComplete = entryComplete;
  }

  public StringBuilder getText() {
    return text;
  }

  public void setText(final StringBuilder text) {
    this.text = text;
  }

  public boolean isCollectText() {
    return collectText;
  }

  public void setCollectText(final boolean collectText) {
    this.collectText = collectText;
  }

  public StringBuilder getCharacters() {
    return characters;
  }

  public void setCharacters(final StringBuilder characters) {
    this.characters = characters;
  }

  public StringBuilder getFormBuffer() {
    return formBuffer;
  }

  public void setFormBuffer(final StringBuilder formBuffer) {
    this.formBuffer = formBuffer;
  }

  public boolean isPrimaryForm() {
    return primaryForm;
  }

  public void setPrimaryForm(final boolean primaryForm) {
    this.primaryForm = primaryForm;
  }

  public boolean isRegularInflection() {
    return regularInflection;
  }

  public void setRegularInflection(final boolean regularInflection) {
    this.regularInflection = regularInflection;
  }

  public StringBuilder getInflParBuffer() {
    return inflParBuffer;
  }

  public void setInflParBuffer(final StringBuilder inflParBuffer) {
    this.inflParBuffer = inflParBuffer;
  }

  public StringBuilder getInflSeqBuffer() {
    return inflSeqBuffer;
  }

  public void setInflSeqBuffer(final StringBuilder inflSeqBuffer) {
    this.inflSeqBuffer = inflSeqBuffer;
  }

  public StringBuilder getSenseGrpBuffer() {
    return senseGrpBuffer;
  }

  public void setSenseGrpBuilder(final StringBuilder senseGrpBuffer) {
    this.senseGrpBuffer = senseGrpBuffer;
  }

  public int getSenseGrpCount() {
    return senseGrpCount;
  }

  public void setSenseGrpCount(final int senseGrpCount) {
    this.senseGrpCount = senseGrpCount;
  }

  public StringBuilder getSenseBuffer() {
    return senseBuffer;
  }

  public void setSenseBuffer(final StringBuilder senseBuffer) {
    this.senseBuffer = senseBuffer;
  }

  public int getSenseCount() {
    return senseCount;
  }

  public void setSenseCount(final int senseCount) {
    this.senseCount = senseCount;
  }

  public boolean isEg() {
    return eg;
  }

  public void setEg(final boolean eg) {
    this.eg = eg;
  }

  public StringBuilder getEgBuffer() {
    return egBuffer;
  }

  public void setEgBuffer(final StringBuilder egBuffer) {
    this.egBuffer = egBuffer;
  }

}
