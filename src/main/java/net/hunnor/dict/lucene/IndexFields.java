package net.hunnor.dict.lucene;

/**
 * Constants for Lucene field names.
 */
public final class IndexFields {

	/**
	 * Hide default constructor.
	 */
	private IndexFields() {
	}

	/**
	 * The language the entry belongs to.
	 */
	public static final String LANG = "lang";

	/**
	 * The ID of the entry.
	 */
	public static final String ID = "id";

	/**
	 * Root forms of the entry.
	 */
	public static final String ROOTS = "roots";

	/**
	 * Inflected forms of the entry.
	 */
	public static final String FORMS = "forms";

	/**
	 * Translations of the entry.
	 */
	public static final String TRANS = "trans";

	/**
	 * Usage examples for the entry.
	 */
	public static final String QUOTE = "quote";

	/**
	 * Translations of usage examples.
	 */
	public static final String QUOTETRANS = "quoteTrans";

	/**
	 * The HTML content of the entry.
	 */
	public static final String TEXT = "text";

	/**
	 * Root forms (Hungarian).
	 */
	public static final String HU_ROOTS = "hu_roots";

	/**
	 * Root forms (Norwegian).
	 */
	public static final String NO_ROOTS = "no_roots";

	/**
	 * Inflected forms (Hungarian).
	 */
	public static final String HU_FORMS = "hu_forms";

	/**
	 * Inflected forms (Norwegian).
	 */
	public static final String NO_FORMS = "no_forms";

	/**
	 * Translations (Hungarian).
	 */
	public static final String HU_TRANS = "hu_trans";

	/**
	 * Translations (Norwegian).
	 */
	public static final String NO_TRANS = "no_trans";

	/**
	 * Usage examples (Hungarian).
	 */
	public static final String HU_QUOTE = "hu_quote";

	/**
	 * Usage examples (Norwegian).
	 */
	public static final String NO_QUOTE = "no_quote";

	/**
	 * Translations of usage examples (Hungarian).
	 */
	public static final String HU_QUOTETRANS = "hu_quoteTrans";

	/**
	 * Translations of usage examples (Norwegian).
	 */
	public static final String NO_QUOTETRANS = "no_quoteTrans";

}
