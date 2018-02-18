package net.hunnor.dict.lucene.model;

import java.util.HashSet;
import java.util.Set;

public class Entry {

  private Language lang;

  private String id;

  private Set<String> roots = new HashSet<>();

  private Set<String> forms = new HashSet<>();

  private Set<String> trans = new HashSet<>();

  private Set<String> quote = new HashSet<>();

  private Set<String> quoteTrans = new HashSet<>();

  private String text;

  public Language getLang() {
    return lang;
  }

  public void setLang(Language lang) {
    this.lang = lang;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Set<String> getRoots() {
    return roots;
  }

  public void setRoots(Set<String> roots) {
    this.roots = roots;
  }

  public Set<String> getForms() {
    return forms;
  }

  public void setForms(Set<String> forms) {
    this.forms = forms;
  }

  public Set<String> getTrans() {
    return trans;
  }

  public void setTrans(Set<String> trans) {
    this.trans = trans;
  }

  public Set<String> getQuote() {
    return quote;
  }

  public void setQuote(Set<String> quote) {
    this.quote = quote;
  }

  public Set<String> getQuoteTrans() {
    return quoteTrans;
  }

  public void setQuoteTrans(Set<String> quoteTrans) {
    this.quoteTrans = quoteTrans;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

}
