package cn.cnic.component.stop.mapper;

import cn.cnic.ApplicationTests;
import cn.cnic.base.utils.LoggerUtil;
import cn.cnic.base.utils.SessionUserUtil;
import cn.cnic.base.utils.UUIDUtils;
import cn.cnic.base.vo.UserVo;
import cn.cnic.common.constant.Constants;
import cn.cnic.component.stopsComponent.entity.StopsComponent;
import cn.cnic.component.stopsComponent.entity.StopsComponentGroup;
import cn.cnic.component.stopsComponent.entity.StopsComponentProperty;
import cn.cnic.component.stopsComponent.mapper.StopsComponentGroupMapper;
import cn.cnic.component.stopsComponent.mapper.StopsComponentMapper;
import cn.cnic.component.stopsComponent.mapper.StopsComponentPropertyMapper;
import cn.cnic.component.stopsComponent.utils.StopsComponentUtils;
import cn.cnic.third.service.IStop;
import cn.cnic.third.vo.stop.ThirdStopsComponentVo;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

public class StopsComponentMapperTest extends ApplicationTests {

  private final Logger logger = LoggerUtil.getLogger();

  private final IStop stopImpl;
  private final StopsComponentMapper stopsComponentMapper;
  private final StopsComponentGroupMapper stopsComponentGroupMapper;
  private final StopsComponentPropertyMapper stopsComponentPropertyMapper;

  @Autowired
  public StopsComponentMapperTest(
      IStop stopImpl,
      StopsComponentMapper stopsComponentMapper,
      StopsComponentGroupMapper stopsComponentGroupMapper,
      StopsComponentPropertyMapper stopsComponentPropertyMapper) {
    this.stopImpl = stopImpl;
    this.stopsComponentMapper = stopsComponentMapper;
    this.stopsComponentGroupMapper = stopsComponentGroupMapper;
    this.stopsComponentPropertyMapper = stopsComponentPropertyMapper;
  }

  @Test
  public void testGetStopsTemplateById() {
    StopsComponent stopsComponent =
        stopsComponentMapper.getStopsComponentById("fbb42f0d8ca14a83bfab13e0ba2d7293");
    if (null == stopsComponent) {
      logger.info("The query result is empty");
      stopsComponent = new StopsComponent();
    }
    logger.info(stopsComponent.toString());
  }

  @Test
  public void testGetStopsPropertyById() {
    StopsComponent stopsComponent =
        stopsComponentMapper.getStopsComponentAndPropertyById("fbb42f0d8ca14a83bfab13e0ba2d7293");
    if (null == stopsComponent) {
      logger.info("The query result is empty");
      stopsComponent = new StopsComponent();
    }
    logger.info(stopsComponent.toString());
  }

  @Test
  public void testGetStopsTemplateListByGroupId() {
    List<StopsComponent> stopsComponentList =
        stopsComponentMapper.getStopsComponentListByGroupId("fbb42f0d8ca14a83bfab13e0ba2d7290");
    if (null == stopsComponentList) {
      logger.info("The query result is empty");
    }
    logger.info(stopsComponentList.size() + "");
  }

  /** Call getAllGroups and save the group */
  @Test
  @Transactional
  @Rollback(value = false)
  public void getStopGroupAndSave() {
    UserVo user = SessionUserUtil.getCurrentUser();
    String username = (null != user) ? user.getUsername() : "-1";

    String[] group = stopImpl.getAllGroup();
    if (null != group && group.length > 0) {
      // The call is successful, the group table information is cleared and then inserted.
      // todo engine to do
      stopsComponentGroupMapper.deleteGroupCorrelation(Constants.ENGIN_FLINK);
      int deleteGroup = stopsComponentGroupMapper.deleteGroup(Constants.ENGIN_FLINK);
      logger.debug("Group" + deleteGroup + "data was successfully deleted！！！");
      int a = 0;
      for (String string : group) {
        if (string.length() > 0) {
          StopsComponentGroup stopGroup = new StopsComponentGroup();
          stopGroup.setId(UUIDUtils.getUUID32());
          stopGroup.setCrtDttm(new Date());
          stopGroup.setCrtUser(username);
          stopGroup.setLastUpdateUser(username);
          stopGroup.setEnableFlag(true);
          stopGroup.setLastUpdateDttm(new Date());
          stopGroup.setGroupName(string);
          int insertStopGroup = stopsComponentGroupMapper.insertStopGroup(stopGroup);
          a += insertStopGroup;
        }
      }
      logger.debug("Group" + a + "data was successfully inserted！！！");
    }
  }

  @Test
  @Transactional
  @Rollback(value = false)
  public void saveStopsAndProperty() {
    // 1.First call the stop interface to get the getAllStops data；
    String[] stopNameList = stopImpl.getAllStops();
    if (null != stopNameList && stopNameList.length > 0) {
      // The call is successful and the Stop message is cleared before insertion
      stopsComponentPropertyMapper.deleteStopsComponentProperty();
      // todo engine todo
      int deleteStopsInfo = stopsComponentMapper.deleteStopsComponent(null);
      logger.info("Successful deletion StopsInfo" + deleteStopsInfo + "piece of data!!!");
      int num = 0;
      for (String stopListInfos : stopNameList) {
        num++;
        // 2.Start by querting stopInfo against the bundle
        logger.info("Now the call is：" + stopListInfos);
        ThirdStopsComponentVo thirdStopsComponentVo = stopImpl.getStopInfo(stopListInfos);
        List<String> list = null;
        if (null != thirdStopsComponentVo) {
          list = Arrays.asList(thirdStopsComponentVo.getGroups().split(","));
        }

        // Query group information according to groupName in stops
        List<StopsComponentGroup> stopGroupByName =
            stopsComponentGroupMapper.getStopGroupByNameList(
                list, thirdStopsComponentVo.getEngineType());

        StopsComponent stopsComponent =
            StopsComponentUtils.thirdStopsComponentVoToStopsTemplate(
                "init", thirdStopsComponentVo, stopGroupByName);
        if (null != stopsComponent) {
          int insertStopsTemplate = stopsComponentMapper.insertStopsComponent(stopsComponent);
          logger.info("flow_stops_template affects the number of rows : " + insertStopsTemplate);
          logger.info(
              "=============================association_groups_stops_template=====start==================");
          List<StopsComponentGroup> stopGroupList = stopsComponent.getStopGroupList();
          for (StopsComponentGroup stopGroup : stopGroupList) {
            String stopGroupId = stopGroup.getId();
            String stopsTemplateId = stopsComponent.getId();
            int insertAssociationGroupsStopsTemplate =
                stopsComponentGroupMapper.insertAssociationGroupsStopsTemplate(
                    stopGroupId, stopsTemplateId, stopGroup.getEngineType());
            logger.info(
                "association_groups_stops_template Association table insertion affects the number of rows : "
                    + insertAssociationGroupsStopsTemplate);
          }
          List<StopsComponentProperty> properties = stopsComponent.getProperties();
          int insertPropertyTemplate =
              stopsComponentPropertyMapper.insertStopsComponentProperty(properties);
          logger.info(
              "flow_stops_property_template affects the number of rows : "
                  + insertPropertyTemplate);
        }
      }
      logger.info(num + "num");
    }
  }
}
