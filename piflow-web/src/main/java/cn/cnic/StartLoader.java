package cn.cnic;

import cn.cnic.base.util.CheckPathUtils;
import cn.cnic.base.util.LoggerUtil;
import cn.cnic.base.util.QuartzUtils;
import cn.cnic.common.Eunm.ScheduleState;
import cn.cnic.common.constant.SysParamsCache;
import cn.cnic.component.system.entity.SysSchedule;
import cn.cnic.component.system.mapper.SysScheduleMapper;
import cn.piflow.Constants;
import java.util.List;
import javax.annotation.Resource;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 1)
public class StartLoader implements ApplicationRunner {

  Logger logger = LoggerUtil.getLogger();

  @Resource private SysScheduleMapper sysScheduleMapper;

  @Resource private Scheduler scheduler;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    checkStoragePath();
    // todo 取消注释
    // startStatusRunning();
  }

  private void checkStoragePath() {
    String storagePathHead = System.getProperty("user.dir");
    logger.warn(storagePathHead);

    CheckPathUtils.isChartPathExist(storagePathHead + "/storage/flink/image/");
    CheckPathUtils.isChartPathExist(storagePathHead + "/storage/flink/video/");
    CheckPathUtils.isChartPathExist(storagePathHead + "/storage/flink/xml/");
    CheckPathUtils.isChartPathExist(storagePathHead + "/storage/flink/csv/");

    CheckPathUtils.isChartPathExist(storagePathHead + "/storage/spark/image/");
    CheckPathUtils.isChartPathExist(storagePathHead + "/storage/spark/video/");
    CheckPathUtils.isChartPathExist(storagePathHead + "/storage/spark/xml/");
    CheckPathUtils.isChartPathExist(storagePathHead + "/storage/spark/csv/");

    SysParamsCache.setImagesPath(
        storagePathHead + "/storage/flink/image/", Constants.ENGIN_FLINK());
    SysParamsCache.setVideosPath(
        storagePathHead + "/storage/flink/video/", Constants.ENGIN_FLINK());
    SysParamsCache.setXmlPath(storagePathHead + "/storage/flink/xml/", Constants.ENGIN_FLINK());
    SysParamsCache.setCsvPath(storagePathHead + "/storage/flink/csv/", Constants.ENGIN_FLINK());

    SysParamsCache.setImagesPath(
        storagePathHead + "/storage/spark/image/", Constants.ENGIN_SPARK());
    SysParamsCache.setVideosPath(
        storagePathHead + "/storage/spark/video/", Constants.ENGIN_SPARK());
    SysParamsCache.setXmlPath(storagePathHead + "/storage/spark/xml/", Constants.ENGIN_SPARK());
    SysParamsCache.setCsvPath(storagePathHead + "/storage/spark/csv/", Constants.ENGIN_SPARK());
  }

  private void startStatusRunning() {
    List<SysSchedule> sysScheduleByStatusList =
        sysScheduleMapper.getSysScheduleListByStatus(true, ScheduleState.RUNNING);
    if (null != sysScheduleByStatusList && sysScheduleByStatusList.size() > 0) {
      for (SysSchedule sysSchedule : sysScheduleByStatusList) {
        QuartzUtils.createScheduleJob(scheduler, sysSchedule);
      }
    }
  }
}
