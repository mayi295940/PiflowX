package cn.cnic.component.flow.mapper;

import cn.cnic.ApplicationTests;
import cn.cnic.base.utils.LoggerUtil;
import cn.cnic.component.flow.entity.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class PathsMapperTest extends ApplicationTests {

  private Logger logger = LoggerUtil.getLogger();

  private final PathsMapper pathsMapper;

  @Autowired
  public PathsMapperTest(PathsMapper pathsMapper) {
    this.pathsMapper = pathsMapper;
  }

  @Test
  public void testGetPathsListByFlowId() {
    List<Paths> pathsList = pathsMapper.getPathsListByFlowId("497d2b3a5b1d4e2da4c8a372779babd5");
    if (null == pathsList) {
      logger.info("The query result is empty");
    } else {
      logger.info(pathsList.size() + "");
    }
  }
}
