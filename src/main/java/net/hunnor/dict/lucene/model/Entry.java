package net.hunnor.dict.lucene.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Model object for a dictionary entry.
 */
public final class Entry {

	/**
	 * The language of the entry.
	 */
	private String lang;

	/**
	 * The ID of the entry.
	 */
	private String id;

	/**
	 * Root forms of the entry.
	 */
	private Set<String> roots = new HashSet<>();

	/**
	 * Inflected forms of the entry.
	 */
	private Set<String> forms = new HashSet<>();

	/**
	 * Translations of the entry.
	 */
	private Set<String> trans = new HashSet<>();

	/**
	 * Usage examples for the entry.
	 */
	private Set<String> quote = new HashSet<>();

	/**
	 * Translations of usage examples.
	 */
	private Set<String> quoteTrans = new HashSet<>();

	/**
	 * The HTML content of the entry.
	 */
	private String text;

	/**
	 * Return the language of the entry.
	 * @return the language of the entry
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * Set the language of the entry.
	 * @param l the language to set
	 */
	public void setLang(final String l) {
		this.lang = l;
	}

	/**
	 * Return the ID of the entry.
	 * @return the ID of the entry.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the ID of the entry.
	 * @param i the ID to set.
	 */
	public void setId(final String i) {
		this.id = i;
	}

	/**
	 * Return the root forms of the entry.
	 * @return root forms of the entry
	 */
	public Set<String> getRoots() {
		return roots;
	}

	/**
	 * Set the root forms of the entry.
	 * @param r the root forms to set
	 */
	public void setRoots(final Set<String> r) {
		this.roots = r;
	}

	/**
	 * Return the inflected forms of the entry.
	 * @return inflected forms of the entry
	 */
	public Set<String> getForms() {
		return forms;
	}

	/**
	 * Set the inflected form of the entry.
	 * @param f the inflected forms to set
	 */
	public void setForms(final Set<String> f) {
		this.forms = f;
	}

	/**
	 * Return translations of the entry.
	 * @return translations of the entry
	 */
	public Set<String> getTrans() {
		return trans;
	}

	/**
	 * Set translations of the entry.
	 * @param t the translations to set
	 */
	public void setTrans(final Set<String> t) {
		this.trans = t;
	}

	/**
	 * Return usage examples for the entry.
	 * @return usage examples for the entry
	 */
	public Set<String> getQuote() {
		return quote;
	}

	/**
	 * Set usage examples of the entry.
	 * @param q the usage examples to set
	 */
	public void setQuote(final Set<String> q) {
		this.quote = q;
	}

	/**
	 * Return translations of usage examples.
	 * @return translations of usage examples
	 */
	public Set<String> getQuoteTrans() {
		return quoteTrans;
	}

	/**
	 * Set translations of usage examples.
	 * @param qt the translations of usage examples to set
	 */
	public void setQuoteTrans(final Set<String> qt) {
		this.quoteTrans = qt;
	}

	/**
	 * Return the HTML content of the entry.
	 * @return the HTML content of the entry
	 */
	public String getText() {
		return text;
	}

	/**
	 * Set the HTML content of the entry.
	 * @param t the HTML content to set
	 */
	public void setText(final String t) {
		this.text = t;
	}

}
