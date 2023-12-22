package cn.cnic.component.stopsComponent.domain;

import cn.cnic.base.utils.LoggerUtil;
import cn.cnic.component.dataSource.mapper.DataSourceMapper;
import cn.cnic.component.stopsComponent.entity.StopsComponent;
import cn.cnic.component.stopsComponent.entity.StopsComponentGroup;
import cn.cnic.component.stopsComponent.entity.StopsComponentProperty;
import cn.cnic.component.stopsComponent.mapper.StopsComponentGroupMapper;
import cn.cnic.component.stopsComponent.mapper.StopsComponentMapper;
import cn.cnic.component.stopsComponent.mapper.StopsComponentPropertyMapper;
import cn.cnic.component.stopsComponent.vo.StopsComponentVo;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(
    propagation = Propagation.REQUIRED,
    isolation = Isolation.DEFAULT,
    timeout = 36000,
    rollbackFor = Exception.class)
public class StopsComponentDomain {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  private final Logger logger = LoggerUtil.getLogger();

  private final StopsComponentPropertyMapper stopsComponentPropertyMapper;
  private final StopsComponentGroupMapper stopsComponentGroupMapper;
  private final StopsComponentMapper stopsComponentMapper;
  private final DataSourceMapper dataSourceMapper;

  @Autowired
  public StopsComponentDomain(
      StopsComponentPropertyMapper stopsComponentPropertyMapper,
      StopsComponentGroupMapper stopsComponentGroupMapper,
      StopsComponentMapper stopsComponentMapper,
      DataSourceMapper dataSourceMapper) {
    this.stopsComponentPropertyMapper = stopsComponentPropertyMapper;
    this.stopsComponentGroupMapper = stopsComponentGroupMapper;
    this.stopsComponentMapper = stopsComponentMapper;
    this.dataSourceMapper = dataSourceMapper;
  }

  public int addListStopsComponentAndChildren(List<StopsComponent> stopsComponentList) {
    if (null == stopsComponentList || stopsComponentList.size() <= 0) {
      return 0;
    }
    int insertRows = 0;
    for (StopsComponent stopsComponent : stopsComponentList) {
      insertRows += addStopsComponentAndChildren(stopsComponent);
    }
    return insertRows;
  }

  public int addStopsComponentAndChildren(StopsComponent stopsComponent) {
    if (null == stopsComponent) {
      return 0;
    }
    int insertStopsComponentRows = stopsComponentMapper.insertStopsComponent(stopsComponent);
    int insertRows = insertStopsComponentRows;
    if (insertStopsComponentRows > 0) {
      List<StopsComponentProperty> properties = stopsComponent.getProperties();
      if (null == properties || properties.size() <= 0) {
        return insertRows;
      }
      int insertStopsTemplateRows =
          stopsComponentPropertyMapper.insertStopsComponentProperty(properties);
      insertRows += insertStopsTemplateRows;
    }
    // Change the corresponding "datasource" data source to available
    if (stopsComponent.getIsDataSource()) {
      dataSourceMapper.updateDataSourceIsAvailableByBundle(1, stopsComponent.getBundle());
      // Modify image url of "datasource"
      dataSourceMapper.updateDataSourceImageUrlByBundle(
          stopsComponent.getBundle(), stopsComponent.getImageUrl());
    } else {
      dataSourceMapper.updateDataSourceIsAvailableByBundle(0, stopsComponent.getBundle());
    }
    return insertRows;
  }

  public int deleteStopsComponentAndChildren(StopsComponent stopsComponent) {
    if (null == stopsComponent) {
      return 0;
    }
    //        int affectedRows = 0;
    //        List<StopsComponentProperty> properties = stopsComponent.getProperties();
    //        if (null != properties && properties.size() > 0) {
    //            affectedRows =
    // stopsComponentPropertyMapper.deleteStopsComponentPropertyByStopId(stopsComponent.getId());
    //        }
    int affectedRows =
        stopsComponentPropertyMapper.deleteStopsComponentPropertyByStopId(stopsComponent.getId());

    int deleteStopsComponentRows =
        stopsComponentMapper.deleteStopsComponentById(stopsComponent.getId());
    affectedRows += deleteStopsComponentRows;
    return affectedRows;
  }

  public int addListStopsComponent(List<StopsComponent> stopsComponentList) {
    if (null == stopsComponentList || stopsComponentList.size() <= 0) {
      return 0;
    }
    int insertRows = 0;
    for (StopsComponent stopsComponent : stopsComponentList) {
      insertRows += addStopsComponent(stopsComponent);
    }
    return insertRows;
  }

  public int addStopsComponent(StopsComponent stopsComponent) {
    if (null == stopsComponent) {
      return 0;
    }
    return stopsComponentMapper.insertStopsComponent(stopsComponent);
  }

  public StopsComponent getStopsComponentById(String id) {
    return stopsComponentMapper.getStopsComponentById(id);
  }

  public StopsComponent getStopsComponentAndPropertyById(String id) {
    return stopsComponentMapper.getStopsComponentAndPropertyById(id);
  }

  public int stopsComponentLinkStopsComponentGroupList(
      StopsComponent stopsComponent, List<StopsComponentGroup> stopsComponentGroupList) {
    if (null == stopsComponent) {
      return 0;
    }
    if (null == stopsComponentGroupList || stopsComponentGroupList.isEmpty()) {
      return 0;
    }
    int affectedRows = 0;
    for (StopsComponentGroup stopGroup : stopsComponentGroupList) {
      String stopGroupId = stopGroup.getId();
      String stopsTemplateId = stopsComponent.getId();
      String engineType = stopsComponent.getEngineType();

      int insertAssociationGroupsStopsTemplate =
          stopsComponentGroupMapper.insertAssociationGroupsStopsTemplate(
              stopGroupId, stopsTemplateId, engineType);
      affectedRows += insertAssociationGroupsStopsTemplate;
      logger.info(
          "association_groups_stops_template Association table insertion affects the number of rows : "
              + insertAssociationGroupsStopsTemplate);
    }
    return affectedRows;
  }

  public int deleteStopsComponentByEngineType(String engineType) {
    stopsComponentPropertyMapper.deleteStopsComponentProperty();
    return stopsComponentMapper.deleteStopsComponent(engineType);
  }

  public int deleteStopsComponent(StopsComponent stopsComponent) {
    if (null == stopsComponent) {
      return 0;
    }
    // delete relationship
    stopsComponentGroupMapper.deleteGroupCorrelationByStopId(stopsComponent.getId());
    logger.debug("Successful delete " + stopsComponent.getName() + " 's association!!!");

    // delete stop
    int affectedRows = deleteStopsComponentAndChildren(stopsComponent);
    logger.debug("Successful delete " + stopsComponent.getName() + " !!!");

    // delete group
    String[] stopsComponentGroup = stopsComponent.getGroups().split(",");
    List<StopsComponentGroup> stopsComponentGroupList =
        stopsComponentGroupMapper.getStopGroupByNameList(
            Arrays.asList(stopsComponentGroup), stopsComponent.getEngineType());

    for (StopsComponentGroup sGroup : stopsComponentGroupList) {

      int count = stopsComponentGroupMapper.getGroupStopCount(sGroup.getId());
      if (count == 0) {
        stopsComponentGroupMapper.deleteGroupById(sGroup.getId());
        logger.debug("Successful delete " + stopsComponent.getName() + " group!!!");
      }
    }
    // Change the corresponding "datasource" data source to unavailable
    dataSourceMapper.updateDataSourceIsAvailableByBundle(0, stopsComponent.getBundle());
    return affectedRows;
  }

  /**
   * getStopsComponentByBundle
   *
   * @param bundle bundle
   */
  public StopsComponent getStopsComponentByBundle(String bundle) {
    return stopsComponentMapper.getStopsComponentByBundle(bundle);
  }

  public int addStopsComponentGroupAndChildren(StopsComponentGroup stopsComponentGroup) {
    if (null == stopsComponentGroup) {
      return 0;
    }
    int insertStopsComponentGroupRows =
        stopsComponentGroupMapper.insertStopGroup(stopsComponentGroup);
    int affectedRows = insertStopsComponentGroupRows;
    if (insertStopsComponentGroupRows > 0) {
      List<StopsComponent> stopsComponentList = stopsComponentGroup.getStopsComponentList();
      if (null == stopsComponentList || stopsComponentList.size() <= 0) {
        return affectedRows;
      }
      int insertStopsTemplateRows = addListStopsComponentAndChildren(stopsComponentList);
      affectedRows = insertStopsComponentGroupRows + insertStopsTemplateRows;
    }
    return affectedRows;
  }

  public int addStopsComponentGroup(StopsComponentGroup stopsComponentGroup) {
    if (null == stopsComponentGroup) {
      return 0;
    }
    return stopsComponentGroupMapper.insertStopGroup(stopsComponentGroup);
  }

  public List<StopsComponentGroup> getStopGroupByNameList(
      List<String> groupNameList, String engineType) {
    return stopsComponentGroupMapper.getStopGroupByNameList(groupNameList, engineType);
  }

  public StopsComponentGroup getStopsComponentGroupByGroupName(String groupName) {
    if (StringUtils.isBlank(groupName)) {
      return null;
    }
    List<StopsComponentGroup> stopGroupByName =
        stopsComponentGroupMapper.getStopGroupByName(groupName);
    if (null == stopGroupByName || stopGroupByName.size() <= 0) {
      return null;
    }
    return stopGroupByName.get(0);
  }

  public int deleteStopsComponentGroup(String engineType) {
    // the group table information is cleared
    // The call is successful, the group table information is cleared and then
    // inserted.
    stopsComponentGroupMapper.deleteGroupCorrelation(engineType);
    int deleteRows = stopsComponentGroupMapper.deleteGroup(engineType);
    logger.debug("Successful deletion Group" + deleteRows + "piece of data!!!");
    return deleteRows;
  }

  public List<StopsComponentGroup> getStopGroupList(String engineType) {
    return stopsComponentGroupMapper.getStopGroupList(engineType);
  }

  public int insertAssociationGroupsStopsTemplate(
      String stopGroupId, String stopsTemplateId, String engineType) {
    return stopsComponentGroupMapper.insertAssociationGroupsStopsTemplate(
        stopGroupId, stopsTemplateId, engineType);
  }

  public List<StopsComponentGroup> getStopGroupByGroupNameList(
      List<String> groupName, String engineType) {
    return stopsComponentGroupMapper.getStopGroupByGroupNameList(groupName, engineType);
  }

  public int deleteGroupCorrelationByGroupIdAndStopId(String stopGroupId, String stopsTemplateId) {
    return stopsComponentGroupMapper.deleteGroupCorrelationByGroupIdAndStopId(
        stopGroupId, stopsTemplateId);
  }

  public List<StopsComponentVo> getManageStopsComponentListByGroupId(String stopGroupId) {
    return stopsComponentMapper.getManageStopsComponentListByGroupId(stopGroupId);
  }

  public List<StopsComponent> getStopsComponentByName(String stopsName) {
    return stopsComponentMapper.getStopsComponentByName(stopsName);
  }

  public List<StopsComponent> getDataSourceStopList() {
    return stopsComponentMapper.getDataSourceStopList();
  }

  public List<StopsComponentProperty> getDataSourceStopsComponentByBundle(
      String stopsTemplateBundle) {
    StopsComponent stopsComponent =
        stopsComponentMapper.getDataSourceStopsComponentByBundle(stopsTemplateBundle);
    return stopsComponent.getProperties();
  }

  public List<StopsComponent> getStopsComponentByStopsHubId(String stopsHubId) {
    return stopsComponentMapper.getStopsComponentByStopsHubId(stopsHubId);
  }

  /**
   * @Description update flow+stops_template @Param stopsComponent @Return int @Author TY @Date
   * 16:32 2023/4/3
   */
  public int updateStopsComponent(StopsComponent stopsComponent) {
    return stopsComponentMapper.updateStopsComponent(stopsComponent);
  }

  /**
   * @Description delete properties @Param stopsId flow_stops_template id @Return int @Author
   * TY @Date 16:36 2023/4/3
   */
  public int deleteStopsComponentProperty(String stopsId) {
    return stopsComponentPropertyMapper.deleteStopsComponentPropertyByStopId(stopsId);
  }

  /**
   * add properties
   *
   * @param stopsComponentPropertyList stopsComponentPropertyList
   */
  public int insertStopsComponentProperty(List<StopsComponentProperty> stopsComponentPropertyList) {
    return stopsComponentPropertyMapper.insertStopsComponentProperty(stopsComponentPropertyList);
  }

  public List<StopsComponent> getSystemDefaultStops(String engineType) {
    return stopsComponentMapper.getSystemDefaultStops(engineType);
  }

  public StopsComponent getOnlyStopsComponentByBundle(String bundle) {
    return stopsComponentMapper.getOnlyStopsComponentByBundle(bundle);
  }

  public List<StopsComponent> getOnlyStopsComponentByBundles(String[] bundles) {
    return stopsComponentMapper.getOnlyStopsComponentByBundles(bundles);
  }
}
