package net.hunnor.dict.lucene.test.constants;

import static org.junit.Assert.assertTrue;

import net.hunnor.dict.lucene.constants.Lucene;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class LuceneTest {

  @Test
  public void testPrivateConstructor() throws Exception {
    Constructor<Lucene> constructor = Lucene.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }

}
