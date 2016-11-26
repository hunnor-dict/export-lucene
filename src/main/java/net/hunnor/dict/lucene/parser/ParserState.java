package net.hunnor.dict.lucene.parser;

import net.hunnor.dict.lucene.model.Entry;

/**
 * Model for recording the progress in parsing an entry.
 */
public final class ParserState {

	/**
	 * The name of the current element.
	 */
	private String element;

	/**
	 * The name of the previous element.
	 */
	private String previous;

	/**
	 * The Entry model with data from the XML.
	 */
	private Entry entry;

	/**
	 * Switch for signaling that the parser finished building an entry.
	 */
	private boolean entryComplete;

	/**
	 * Buffer for the HTML content of the entry.
	 */
	private StringBuilder text = new StringBuilder();

	/**
	 * Switch for collecting content from text nodes.
	 */
	private boolean collectText;

	/**
	 * Buffer for text collected from text nodes.
	 */
	private StringBuilder characters = new StringBuilder();

	/**
	 * Buffer for the content of 'form'.
	 */
	private StringBuilder formBuffer = new StringBuilder();

	/**
	 * Switch for signaling that the form is a primary form.
	 */
	private boolean primaryForm;

	/**
	 * Switch for signaling if the entry has regular inflection.
	 */
	private boolean regularInflection;

	/**
	 * Buffer for the content of 'inflPar'.
	 */
	private StringBuilder inflParBuffer = new StringBuilder();

	/**
	 * Buffer for the content of 'inflSeq'.
	 */
	private StringBuilder inflSeqBuffer = new StringBuilder();

	/**
	 * Buffer for the content of 'senseGrp'.
	 */
	private StringBuilder senseGrpBuffer = new StringBuilder();

	/**
	 * Counter for 'senseGrp'.
	 */
	private int senseGrpCount;

	/**
	 * Buffer for the content of 'sense'.
	 */
	private StringBuilder senseBuffer = new StringBuilder();

	/**
	 * Counter for 'sense'.
	 */
	private int senseCount;

	/**
	 * Switch for signaling that the content is an example.
	 */
	private boolean eg;

	/**
	 * Buffer for the content of 'eg'.
	 */
	private StringBuilder egBuffer = new StringBuilder();

	/**
	 * Return the name of the current element.
	 * @return the name of the current element
	 */
	public String getElement() {
		return element;
	}

	/**
	 * Set the name of the current element.
	 * @param e the name to set
	 */
	public void setElement(final String e) {
		this.element = e;
	}

	/**
	 * Return the name of the previous element.
	 * @return the name of the previous element
	 */
	public String getPrevious() {
		return previous;
	}

	/**
	 * Set the name of the previous element.
	 * @param p the name to set
	 */
	public void setPrevious(final String p) {
		this.previous = p;
	}

	/**
	 * Return the entry model.
	 * @return the entry model
	 */
	public Entry getEntry() {
		return entry;
	}

	/**
	 * Set the entry model.
	 * @param e the entry model to set
	 */
	public void setEntry(final Entry e) {
		this.entry = e;
	}

	/**
	 * Return if the entry is complete.
	 * @return true if the entry is complete, false otherwise
	 */
	public boolean isEntryComplete() {
		return entryComplete;
	}

	/**
	 * Set if the entry is complete.
	 * @param ec the value to set
	 */
	public void setEntryComplete(final boolean ec) {
		this.entryComplete = ec;
	}

	/**
	 * Return the content buffer.
	 * @return the content buffer
	 */
	public StringBuilder getText() {
		return text;
	}

	/**
	 * Set the content buffer.
	 * @param t the content buffer to set
	 */
	public void setText(final StringBuilder t) {
		this.text = t;
	}

	/**
	 * Return if text should be collected.
	 * @return true if text should be collected, false otherwise
	 */
	public boolean isCollectText() {
		return collectText;
	}

	/**
	 * Set if text should be collected.
	 * @param ct the value to set
	 */
	public void setCollectText(final boolean ct) {
		this.collectText = ct;
	}

	/**
	 * Return the text buffer.
	 * @return the text buffer
	 */
	public StringBuilder getCharacters() {
		return characters;
	}

	/**
	 * Set the text buffer.
	 * @param c the text buffer to set
	 */
	public void setCharacters(final StringBuilder c) {
		this.characters = c;
	}

	/**
	 * Return the buffer for the content of 'form'.
	 * @return the content buffer
	 */
	public StringBuilder getFormBuffer() {
		return formBuffer;
	}

	/**
	 * Set the buffer for the content of 'form'.
	 * @param fb the buffer to set
	 */
	public void setFormBuffer(final StringBuilder fb) {
		this.formBuffer = fb;
	}

	/**
	 * Return if the form is a primary form.
	 * @return true if the form is a primary form, false otherwise
	 */
	public boolean isPrimaryForm() {
		return primaryForm;
	}

	/**
	 * Set if the form is a primary form.
	 * @param pf the value to set
	 */
	public void setPrimaryForm(final boolean pf) {
		this.primaryForm = pf;
	}

	/**
	 * Return if the entry has regular inflection.
	 * @return true if the entry has regular inflection, false otherwise
	 */
	public boolean isRegularInflection() {
		return regularInflection;
	}

	/**
	 * Set if the entry has regular inflection.
	 * @param ri the value to set
	 */
	public void setRegularInflection(final boolean ri) {
		this.regularInflection = ri;
	}

	/**
	 * Return the buffer for the content of 'inflPar'.
	 * @return the content buffer
	 */
	public StringBuilder getInflParBuffer() {
		return inflParBuffer;
	}

	/**
	 * Set the buffer for the content of 'inflPar'.
	 * @param ipb the buffer to set
	 */
	public void setInflParBuffer(final StringBuilder ipb) {
		this.inflParBuffer = ipb;
	}

	/**
	 * Return the buffer for the content of 'inflSeq'.
	 * @return the content buffer
	 */
	public StringBuilder getInflSeqBuffer() {
		return inflSeqBuffer;
	}

	/**
	 * Set the buffer for the content of 'inflSeq'.
	 * @param isb the buffer to set
	 */
	public void setInflSeqBuffer(final StringBuilder isb) {
		this.inflSeqBuffer = isb;
	}

	/**
	 * Return the buffer for the content of 'senseGrp'.
	 * @return the content buffer
	 */
	public StringBuilder getSenseGrpBuffer() {
		return senseGrpBuffer;
	}

	/**
	 * Set the buffer for the content of 'senseGrp'.
	 * @param sgb the buffer to set
	 */
	public void setSenseGrpBuilder(final StringBuilder sgb) {
		this.senseGrpBuffer = sgb;
	}

	/**
	 * Return the counter value for 'senseGrp'.
	 * @return the counter value for 'senseGrp'
	 */
	public int getSenseGrpCount() {
		return senseGrpCount;
	}

	/**
	 * Set the counter value for 'senseGrp'.
	 * @param sgc the value to set
	 */
	public void setSenseGrpCount(final int sgc) {
		this.senseGrpCount = sgc;
	}

	/**
	 * Return the buffer for the content of 'sense'.
	 * @return the content buffer
	 */
	public StringBuilder getSenseBuffer() {
		return senseBuffer;
	}

	/**
	 * Set the buffer for the content of 'sense'.
	 * @param sb the buffer to set
	 */
	public void setSenseBuffer(final StringBuilder sb) {
		this.senseBuffer = sb;
	}

	/**
	 * Return the counter value for 'sense'.
	 * @return the counter value for 'sense'
	 */
	public int getSenseCount() {
		return senseCount;
	}

	/**
	 * Set the counter value for 'sense'.
	 * @param sc the value to set
	 */
	public void setSenseCount(final int sc) {
		this.senseCount = sc;
	}

	/**
	 * Return if the content is an example.
	 * @return true if the content is an example, false otherwise
	 */
	public boolean isEg() {
		return eg;
	}

	/**
	 * Set if the content is an example.
	 * @param e the value to set
	 */
	public void setEg(final boolean e) {
		this.eg = e;
	}

	/**
	 * Return the buffer for the content of 'eg'.
	 * @return the content buffer
	 */
	public StringBuilder getEgBuffer() {
		return egBuffer;
	}

	/**
	 * Set the buffer for the content of 'eg'.
	 * @param eb the buffer to set
	 */
	public void setEgBuffer(final StringBuilder eb) {
		this.egBuffer = eb;
	}

}
