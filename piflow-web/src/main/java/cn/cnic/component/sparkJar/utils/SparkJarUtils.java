package cn.cnic.component.sparkJar.utils;

import cn.cnic.component.sparkJar.model.SparkJarComponent;
import java.util.Date;

public class SparkJarUtils {

  public static SparkJarComponent sparkJarNewNoId(String username) {

    SparkJarComponent sparkJarComponent = new SparkJarComponent();
    // basic properties (required when creating)
    sparkJarComponent.setCrtDttm(new Date());
    sparkJarComponent.setCrtUser(username);
    // basic properties
    sparkJarComponent.setEnableFlag(true);
    sparkJarComponent.setLastUpdateUser(username);
    sparkJarComponent.setLastUpdateDttm(new Date());
    sparkJarComponent.setVersion(0L);
    return sparkJarComponent;
  }
}
