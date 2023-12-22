package cn.cnic.component.dashboard.service;

import cn.cnic.ApplicationTests;
import cn.cnic.base.utils.ReturnMapUtils;
import net.sf.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

public class ResourceServiceTest extends ApplicationTests {

  private final IResourceService resourceServiceImpl;

  @Autowired
  public ResourceServiceTest(IResourceService resourceServiceImpl) {
    this.resourceServiceImpl = resourceServiceImpl;
  }

  @Test
  @Rollback(false)
  public void testGetResourceInfo() {
    String result = resourceServiceImpl.getResourceInfo();

    JSONObject resourceInfoObj = JSONObject.fromObject(result);
    String str = ReturnMapUtils.setSucceededCustomParamRtnJsonStr("resourceInfo", resourceInfoObj);
    System.out.println(str);
  }
}
