package cn.cnic.component.testData.utils;

import cn.cnic.base.utils.UUIDUtils;
import cn.cnic.component.testData.entity.TestDataSchema;
import cn.cnic.controller.requestVo.TestDataSchemaVoRequest;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

public class TestDataSchemaUtils {

  public static TestDataSchema setTestDataSchemaBasicInformation(
      TestDataSchema testDataSchema, boolean isSetId, String username) {
    if (null == testDataSchema) {
      testDataSchema = new TestDataSchema();
    }
    if (isSetId) {
      testDataSchema.setId(UUIDUtils.getUUID32());
    }
    // set MxGraphModel basic information
    testDataSchema.setCrtDttm(new Date());
    testDataSchema.setCrtUser(username);
    testDataSchema.setLastUpdateDttm(new Date());
    testDataSchema.setLastUpdateUser(username);
    testDataSchema.setVersion(0L);
    return testDataSchema;
  }

  /**
   * testDataSchemaVo data to testDataSchema
   *
   * @param testDataSchemaVo
   * @param testDataSchema
   * @return
   */
  public static TestDataSchema copyDataToTestDataSchema(
      TestDataSchemaVoRequest testDataSchemaVo, TestDataSchema testDataSchema, String username) {
    if (null == testDataSchemaVo || StringUtils.isBlank(username)) {
      return null;
    }
    if (null == testDataSchema) {
      return null;
    }
    // copy
    BeanUtils.copyProperties(testDataSchemaVo, testDataSchema);
    testDataSchema.setLastUpdateDttm(new Date());
    testDataSchema.setLastUpdateUser(username);
    return testDataSchema;
  }
}
