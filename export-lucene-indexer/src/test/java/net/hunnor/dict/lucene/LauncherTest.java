package net.hunnor.dict.lucene;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Launcher.class)
public class LauncherTest {

  @Test(expected = RuntimeException.class)
  public void testIncompleteArguments() {
    String[] args = new String[] {"-x", "file.xml"};
    Launcher.main(args);
  }

  @Test(expected = RuntimeException.class)
  public void testInvalidArguments() {
    String[] args = new String[] {"-l", "h", "-x", "file.xml", "-d", "index", "-s", "speling"};
    Launcher.main(args);
  }

  @Test(expected = InvalidArgumentsException.class)
  public void testValidArgiments() throws Exception {
    Service mockService = Mockito.mock(Service.class);
    PowerMockito.mockStatic(Launcher.class);
    PowerMockito.when(Launcher.class, "getService").thenReturn(mockService);
    PowerMockito.when(Launcher.class, "main", Matchers.any()).thenCallRealMethod();
    String[] args = new String[] {"-l", "hu", "-x", "file.xml", "-d", "index", "-s", "speling"};
    Launcher.main(args);
  }

  @Test
  public void testServiceInitialization() throws NoSuchMethodException, SecurityException,
      IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Method method = Launcher.class.getDeclaredMethod("getService");
    method.setAccessible(true);
    Object service = method.invoke(null, new Object[] {});
    assertEquals(Service.class, service.getClass());
  }

}
