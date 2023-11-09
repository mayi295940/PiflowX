package cn.cnic.component.dashboard.service;

import cn.cnic.ApplicationTests;
import cn.cnic.base.util.ReturnMapUtils;
import javax.annotation.Resource;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;

public class ResourceServiceTest extends ApplicationTests {

  @Resource private IResourceService resourceServiceImpl;

  @Test
  @Rollback(false)
  public void testGetResourceInfo() {
    String result = resourceServiceImpl.getResourceInfo();

    JSONObject resourceInfoObj = JSONObject.fromObject(result);
    String str = ReturnMapUtils.setSucceededCustomParamRtnJsonStr("resourceInfo", resourceInfoObj);
    System.out.println(str);
  }
}
