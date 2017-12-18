package net.hunnor.dict.lucene.test.parser;

import static org.junit.Assert.assertTrue;

import net.hunnor.dict.lucene.parser.TagNames;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class TagNamesTest {

  @Test
  public void testPrivateConstructor() throws Exception {
    Constructor<TagNames> constructor = TagNames.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }

}
