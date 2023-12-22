package cn.cnic.component.sparkJar.service;

import cn.cnic.ApplicationTests;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

public class SparkJarTest extends ApplicationTests {

  private final ISparkJarService sparkJarServiceImpl;

  @Autowired
  public SparkJarTest(ISparkJarService sparkJarServiceImpl) {
    this.sparkJarServiceImpl = sparkJarServiceImpl;
  }

  @Test
  @Rollback(false)
  public void testMountSparkJar() {
    String result = sparkJarServiceImpl.mountSparkJar("Nature", true, "111");
    System.out.println(result);
  }

  @Test
  @Rollback(false)
  public void testUnMountSparkJar() {
    String result = sparkJarServiceImpl.unmountSparkJar("Nature", true, "111");
    System.out.println(result);
  }
}
