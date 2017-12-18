package net.hunnor.dict.lucene.test.analyzer;

import static org.junit.Assert.assertTrue;

import net.hunnor.dict.lucene.analyzer.PerFieldAnalyzer;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class PerFieldAnalyzerTest {

  @Test
  public void testPrivateConstructor() throws Exception {
    Constructor<PerFieldAnalyzer> constructor = PerFieldAnalyzer.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }

}
