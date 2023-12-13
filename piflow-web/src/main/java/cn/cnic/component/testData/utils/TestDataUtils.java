package cn.cnic.component.testData.utils;

import cn.cnic.base.util.UUIDUtils;
import cn.cnic.component.testData.entity.TestData;
import java.util.Date;

public class TestDataUtils {

  public static TestData setTestDataBasicInformation(
      TestData testData, boolean isSetId, String username) {
    if (null == testData) {
      testData = new TestData();
    }
    if (isSetId) {
      testData.setId(UUIDUtils.getUUID32());
    }
    // set MxGraphModel basic information
    testData.setCrtDttm(new Date());
    testData.setCrtUser(username);
    testData.setLastUpdateDttm(new Date());
    testData.setLastUpdateUser(username);
    testData.setVersion(0L);
    return testData;
  }
}