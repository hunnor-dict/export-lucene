package net.hunnor.dict.lucene;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class IndexObject {

	public static final String FIELD_LANG = "lang";
	public static final String FIELD_ID = "id";
	public static final String FIELD_ROOTS = "roots";
	public static final String FIELD_FORMS = "forms";
	public static final String FIELD_TRANS = "trans";
	public static final String FIELD_QUOTE = "quote";
	public static final String FIELD_QUOTETRANS = "quoteTrans";

	public static final String FIELD_TEXT = "text";

	public static final String LUCENE_FIELD_HU_ROOTS = "hu_roots";
	public static final String LUCENE_FIELD_NO_ROOTS = "no_roots";
	public static final String LUCENE_FIELD_HU_FORMS = "hu_forms";
	public static final String LUCENE_FIELD_NO_FORMS = "no_forms";
	public static final String LUCENE_FIELD_HU_TRANS = "hu_trans";
	public static final String LUCENE_FIELD_NO_TRANS = "no_trans";
	public static final String LUCENE_FIELD_HU_QUOTE = "hu_quote";
	public static final String LUCENE_FIELD_NO_QUOTE = "no_quote";
	public static final String LUCENE_FIELD_HU_QUOTETRANS = "hu_quoteTrans";
	public static final String LUCENE_FIELD_NO_QUOTETRANS = "no_quoteTrans";

	private String lang;
	private String id;
	private List<String> roots;
	private List<String> forms;
	private List<String> trans;
	private List<String> quote;
	private List<String> quoteTrans;

	private String text;

	public IndexObject() {
	}

	public IndexObject(Document document) {
		this.lang = document.get(FIELD_LANG);
		this.id = document.get(FIELD_ID);

		String[] roots = document.getValues(FIELD_ROOTS);
		if (roots.length > 0) {
			List<String> rootList = new ArrayList<>();
			for (String root: roots) {
				rootList.add(root);
			}
			this.setRoots(rootList);
		}

		String[] forms = document.getValues(FIELD_FORMS);
		if (forms.length > 0) {
			List<String> formList = new ArrayList<>();
			for (String form: forms) {
				formList.add(form);
			}
			this.setForms(formList);
		}

		String[] trans = document.getValues(FIELD_TRANS);
		if (trans.length > 0) {
			List<String> transList = new ArrayList<>();
			for (String tr: trans) {
				transList.add(tr);
			}
			this.setTrans(transList);
		}

		String[] quote = document.getValues(FIELD_QUOTE);
		if (quote.length > 0) {
			List<String> quoteList = new ArrayList<>();
			for (String q: quote) {
				quoteList.add(q);
			}
			this.setQuote(quoteList);
		}

		String[] quoteTrans = document.getValues(FIELD_QUOTETRANS);
		if (quoteTrans.length > 0) {
			List<String> quoteTransList = new ArrayList<>();
			for (String qTr: quoteTrans) {
				quoteTransList.add(qTr);
			}
			this.setQuoteTrans(quoteTransList);
		}

		this.text = document.get(FIELD_TEXT);
	}

	public Document toLuceneDocument() {
		String rootsField = LUCENE_FIELD_HU_ROOTS;
		String formsField = LUCENE_FIELD_HU_FORMS;
		String transField = LUCENE_FIELD_HU_TRANS;
		String quoteField = LUCENE_FIELD_HU_QUOTE;
		String quoteTransField = LUCENE_FIELD_HU_QUOTETRANS;
		if ("no".equals(lang)) {
			rootsField = LUCENE_FIELD_NO_ROOTS;
			formsField = LUCENE_FIELD_NO_FORMS;
			transField = LUCENE_FIELD_NO_TRANS;
			quoteField = LUCENE_FIELD_NO_QUOTE;
			quoteTransField = LUCENE_FIELD_NO_QUOTETRANS;
		}

		Document document = new Document();
		if (lang != null) {
			document.add(new Field(FIELD_LANG, lang, Field.Store.YES, Field.Index.ANALYZED));
		}
		if (id != null) {
			document.add(new Field(FIELD_ID, id, Field.Store.YES, Field.Index.ANALYZED));
		}
		if (roots != null) {
			for (String root: roots) {
				document.add(new Field(rootsField, root, Field.Store.YES, Field.Index.ANALYZED));
			}
		}
		if (forms != null) {
			for (String form: forms) {
				document.add(new Field(formsField, form, Field.Store.NO, Field.Index.ANALYZED));
			}
		}
		if (trans != null) {
			for (String tr: trans) {
				document.add(new Field(transField, tr, Field.Store.NO, Field.Index.ANALYZED));
			}
		}
		if (quote != null) {
			for (String q: quote) {
				document.add(new Field(quoteField, q, Field.Store.NO, Field.Index.ANALYZED));
			}
		}
		if (quoteTrans != null) {
			for (String qTr: quoteTrans) {
				document.add(new Field(quoteTransField, qTr, Field.Store.NO, Field.Index.ANALYZED));
			}
		}

		if (text != null) {
			document.add(new Field(FIELD_TEXT, text, Field.Store.YES, Field.Index.NOT_ANALYZED));
		}

		return document;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public List<String> getRoots() {
		return roots;
	}

	public void setRoots(List<String> roots) {
		this.roots = roots;
	}

	public List<String> getForms() {
		return forms;
	}

	public void setForms(List<String> forms) {
		this.forms = forms;
	}

	public List<String> getTrans() {
		return trans;
	}

	public void setTrans(List<String> trans) {
		this.trans = trans;
	}

	public List<String> getQuote() {
		return quote;
	}

	public void setQuote(List<String> quote) {
		this.quote = quote;
	}

	public List<String> getQuoteTrans() {
		return quoteTrans;
	}

	public void setQuoteTrans(List<String> quoteTrans) {
		this.quoteTrans = quoteTrans;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
