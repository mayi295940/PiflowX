package cn.cnic.component.flow.service;

import cn.cnic.ApplicationTests;
import cn.cnic.base.utils.LoggerUtil;
import cn.cnic.base.utils.UUIDUtils;
import cn.cnic.component.flow.entity.Flow;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

public class FlowServiceTest extends ApplicationTests {

  private Logger logger = LoggerUtil.getLogger();

  private final IFlowService flowService;

  @Autowired
  public FlowServiceTest(IFlowService flowService) {
    this.flowService = flowService;
  }

  @Test
  @Rollback(false)
  public void testGetFlowById() {
    Flow flow = flowService.getFlowById("admin", true, "85f90a18423245b09cde371cbb333021");
    if (null == flow) {
      logger.info("The query result is empty");
      flow = new Flow();
    }
    logger.info(flow.toString());
  }

  @Test
  @Rollback(false)
  public void testAddFlow() {
    Flow flow = new Flow();
    flow.setId(UUIDUtils.getUUID32());
    // flow.setAppId("kongkong");
    flow.setCrtUser("Nature");
    flow.setLastUpdateUser("Nature");
    flow.setUuid(flow.getId());
    flow.setName("testFlow");
    // StatefulRtnBase addFlow = flowService.addFlow(MxGraphModel mxGraphModel,
    // String flowId);
    // logger.info(addFlow + "");
  }
}
