package cn.cnic.third;

import cn.cnic.ApplicationTests;
import cn.cnic.base.utils.LoggerUtil;
import cn.cnic.component.flow.entity.Flow;
import cn.cnic.component.flow.mapper.FlowMapper;
import cn.cnic.component.process.entity.Process;
import cn.cnic.component.process.utils.ProcessUtils;
import cn.cnic.component.schedule.entity.Schedule;
import cn.cnic.component.schedule.utils.ScheduleUtils;
import cn.cnic.third.service.ISchedule;
import cn.cnic.third.vo.schedule.ThirdScheduleVo;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class IScheduleTest extends ApplicationTests {

  private Logger logger = LoggerUtil.getLogger();

  private final ISchedule scheduleImpl;
  private final FlowMapper flowMapper;

  @Autowired
  public IScheduleTest(ISchedule scheduleImpl, FlowMapper flowMapper) {
    this.scheduleImpl = scheduleImpl;
    this.flowMapper = flowMapper;
  }

  @Test
  public void scheduleStartTest() {
    Schedule schedule = ScheduleUtils.newScheduleNoId("admin");
    schedule.setType("FLOW");
    schedule.setCronExpression("0 0/5 * * * ?");
    schedule.setScheduleRunTemplateId("0641076d5ae840c09d2be5b71fw00001");
    // query
    Flow flowById = flowMapper.getFlowById("0641076d5ae840c09d2be5b71fw00001");
    // flow convert process
    Process process = ProcessUtils.flowToProcess(flowById, "admin", true);
    Map<String, Object> scheduleId = scheduleImpl.scheduleStart(schedule, process, null);
    logger.info(scheduleId.get("scheduleId").toString());
  }

  @Test
  public void scheduleStopTest() {
    String s = scheduleImpl.scheduleStop("schedule_40d04683-40cc-479f-a2d2-1ad40e87eef2");
    if (StringUtils.isNotBlank(s) && s.contains("ok!")) {
      logger.info(s);
    } else {
      logger.info("failed");
    }
  }

  @Test
  public void scheduleInfoTest() {
    ThirdScheduleVo s = scheduleImpl.scheduleInfo("schedule_40d04683-40cc-479f-a2d2-1ad40e87eef2");
    logger.info(s.toString());
  }
}
