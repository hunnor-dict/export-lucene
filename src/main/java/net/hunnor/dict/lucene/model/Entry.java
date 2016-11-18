package net.hunnor.dict.lucene.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import net.hunnor.dict.lucene.IndexFields;

/**
 * Model object for a dictionary entry.
 */
public class Entry {

	private String lang;
	private String id;
	private List<String> roots;
	private List<String> forms;
	private List<String> trans;
	private List<String> quote;
	private List<String> quoteTrans;

	private String text;

	public Entry() {
	}

	public Entry(Document document) {
		this.lang = document.get(IndexFields.LANG);
		this.id = document.get(IndexFields.ID);

		String[] roots = document.getValues(IndexFields.ROOTS);
		if (roots.length > 0) {
			List<String> rootList = new ArrayList<>();
			for (String root: roots) {
				rootList.add(root);
			}
			this.setRoots(rootList);
		}

		String[] forms = document.getValues(IndexFields.FORMS);
		if (forms.length > 0) {
			List<String> formList = new ArrayList<>();
			for (String form: forms) {
				formList.add(form);
			}
			this.setForms(formList);
		}

		String[] trans = document.getValues(IndexFields.TRANS);
		if (trans.length > 0) {
			List<String> transList = new ArrayList<>();
			for (String tr: trans) {
				transList.add(tr);
			}
			this.setTrans(transList);
		}

		String[] quote = document.getValues(IndexFields.QUOTE);
		if (quote.length > 0) {
			List<String> quoteList = new ArrayList<>();
			for (String q: quote) {
				quoteList.add(q);
			}
			this.setQuote(quoteList);
		}

		String[] quoteTrans = document.getValues(IndexFields.QUOTETRANS);
		if (quoteTrans.length > 0) {
			List<String> quoteTransList = new ArrayList<>();
			for (String qTr: quoteTrans) {
				quoteTransList.add(qTr);
			}
			this.setQuoteTrans(quoteTransList);
		}

		this.text = document.get(IndexFields.TEXT);
	}

	public Document toLuceneDocument() {
		String rootsField = IndexFields.HU_ROOTS;
		String formsField = IndexFields.HU_FORMS;
		String transField = IndexFields.HU_TRANS;
		String quoteField = IndexFields.HU_QUOTE;
		String quoteTransField = IndexFields.HU_QUOTETRANS;
		if ("no".equals(lang)) {
			rootsField = IndexFields.NO_ROOTS;
			formsField = IndexFields.NO_FORMS;
			transField = IndexFields.NO_TRANS;
			quoteField = IndexFields.NO_QUOTE;
			quoteTransField = IndexFields.NO_QUOTETRANS;
		}

		Document document = new Document();
		if (lang != null) {
			document.add(new Field(IndexFields.LANG, lang, Field.Store.YES, Field.Index.ANALYZED));
		}
		if (id != null) {
			document.add(new Field(IndexFields.ID, id, Field.Store.YES, Field.Index.ANALYZED));
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
			document.add(new Field(IndexFields.TEXT, text, Field.Store.YES, Field.Index.NOT_ANALYZED));
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
