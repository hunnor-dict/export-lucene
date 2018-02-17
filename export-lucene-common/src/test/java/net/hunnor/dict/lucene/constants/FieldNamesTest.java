package net.hunnor.dict.lucene.constants;

import static org.junit.Assert.assertTrue;

import net.hunnor.dict.lucene.constants.FieldNames;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class FieldNamesTest {

  @Test
  public void testPrivateConstructor() throws Exception {
    Constructor<FieldNames> constructor = FieldNames.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
    constructor.setAccessible(true);
    constructor.newInstance();
  }

}
