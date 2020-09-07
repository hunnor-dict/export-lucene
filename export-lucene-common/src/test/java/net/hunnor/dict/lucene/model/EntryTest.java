package net.hunnor.dict.lucene.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

class EntryTest {

  @Test
  void testLanguage() {
    Entry entry = new Entry();
    entry.setLang(Language.HU);
    assertEquals(Language.HU, entry.getLang());
  }

  @Test
  void testId() {
    Entry entry = new Entry();
    entry.setId("1");
    assertEquals("1", entry.getId());
  }

  @Test
  void testRoots() {
    Entry entry = new Entry();
    entry.setRoots(new HashSet<String>(Arrays.asList(new String[] {"root"})));
    assertEquals(1, entry.getRoots().size());
    assertTrue(entry.getRoots().contains("root"));
  }

  @Test
  void testForms() {
    Entry entry = new Entry();
    entry.setForms(new HashSet<String>(Arrays.asList(new String[] {"form"})));
    assertEquals(1, entry.getForms().size());
    assertTrue(entry.getForms().contains("form"));
  }

  @Test
  void testTrans() {
    Entry entry = new Entry();
    entry.setTrans(new HashSet<String>(Arrays.asList(new String[] {"trans"})));
    assertEquals(1, entry.getTrans().size());
    assertTrue(entry.getTrans().contains("trans"));
  }

  @Test
  void testQuote() {
    Entry entry = new Entry();
    entry.setQuote(new HashSet<String>(Arrays.asList(new String[] {"quote"})));
    assertEquals(1, entry.getQuote().size());
    assertTrue(entry.getQuote().contains("quote"));
  }

  @Test
  void testQuoteTrans() {
    Entry entry = new Entry();
    entry.setQuoteTrans(new HashSet<String>(Arrays.asList(new String[] {"quoteTrans"})));
    assertEquals(1, entry.getQuoteTrans().size());
    assertTrue(entry.getQuoteTrans().contains("quoteTrans"));
  }

  @Test
  void testSort() {
    Entry entry = new Entry();
    entry.setSort("sort");
    assertEquals("sort", entry.getSort());
  }

  @Test
  void testText() {
    Entry entry = new Entry();
    entry.setText("<html/>");
    assertEquals("<html/>", entry.getText());
  }

}
