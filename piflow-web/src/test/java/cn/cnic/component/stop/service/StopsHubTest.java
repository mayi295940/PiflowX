package cn.cnic.component.stop.service;

import cn.cnic.ApplicationTests;
import cn.cnic.component.stopsComponent.service.IStopsHubService;
import javax.annotation.Resource;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;

public class StopsHubTest extends ApplicationTests {

  @Resource private IStopsHubService stopsHubServiceImpl;

  @Test
  @Rollback(false)
  public void testMountStopsHub() {
    String result = stopsHubServiceImpl.mountStopsHub("Nature", true, "222");
    System.out.println(result);
  }

  @Test
  @Rollback(false)
  public void testUnMountStopsHub() {
    String result = stopsHubServiceImpl.unmountStopsHub("Nature", true, "222");
    System.out.println(result);
  }
}
