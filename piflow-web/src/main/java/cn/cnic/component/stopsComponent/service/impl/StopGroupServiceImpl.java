package cn.cnic.component.stopsComponent.service.impl;

import cn.cnic.base.util.LoggerUtil;
import cn.cnic.base.util.ReturnMapUtils;
import cn.cnic.base.util.UUIDUtils;
import cn.cnic.component.flow.entity.Flow;
import cn.cnic.component.flow.mapper.FlowMapper;
import cn.cnic.component.stopsComponent.domain.StopsComponentDomain;
import cn.cnic.component.stopsComponent.domain.StopsComponentGroupDomain;
import cn.cnic.component.stopsComponent.model.StopsComponent;
import cn.cnic.component.stopsComponent.model.StopsComponentGroup;
import cn.cnic.component.stopsComponent.model.StopsComponentProperty;
import cn.cnic.component.stopsComponent.service.IStopGroupService;
import cn.cnic.component.stopsComponent.utils.StopsComponentGroupUtils;
import cn.cnic.component.stopsComponent.utils.StopsComponentUtils;
import cn.cnic.component.stopsComponent.vo.PropertyTemplateVo;
import cn.cnic.component.stopsComponent.vo.StopGroupVo;
import cn.cnic.component.stopsComponent.vo.StopsComponentGroupVo;
import cn.cnic.component.stopsComponent.vo.StopsComponentVo;
import cn.cnic.component.stopsComponent.vo.StopsTemplateVo;
import cn.cnic.third.service.IStop;
import cn.cnic.third.vo.stop.ThirdStopsComponentVo;
import cn.piflow.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StopGroupServiceImpl implements IStopGroupService {

  Logger logger = LoggerUtil.getLogger();

  @Autowired private IStop stopImpl;

  @Autowired private StopsComponentGroupDomain stopsComponentGroupDomain;

  @Autowired private StopsComponentDomain stopsComponentDomain;

  @Resource private FlowMapper flowMapper;

  /**
   * Query all groups and all stops under it
   *
   * @return StopGroupVo list
   */
  @Override
  public List<StopGroupVo> getStopGroupAll(String engineType) {
    List<StopsComponentGroup> stopGroupList = stopsComponentDomain.getStopGroupList(engineType);
    if (CollectionUtils.isEmpty(stopGroupList)) {
      return null;
    }
    List<StopGroupVo> stopGroupVoList = new ArrayList<>();
    for (StopsComponentGroup stopGroup : stopGroupList) {
      if (null == stopGroup) {
        continue;
      }
      List<StopsComponent> stopsComponentList = stopGroup.getStopsComponentList();
      if (null == stopsComponentList || stopsComponentList.size() <= 0) {
        continue;
      }
      StopGroupVo stopGroupVo = new StopGroupVo();
      BeanUtils.copyProperties(stopGroup, stopGroupVo);

      List<StopsTemplateVo> stopsTemplateVoList = new ArrayList<>();
      for (StopsComponent stopsComponent : stopsComponentList) {
        if (null == stopsComponent) {
          continue;
        }
        StopsTemplateVo stopsTemplateVo = new StopsTemplateVo();
        BeanUtils.copyProperties(stopsComponent, stopsTemplateVo);
        List<StopsComponentProperty> properties = stopsComponent.getProperties();
        if (null != properties && properties.size() > 0) {
          List<PropertyTemplateVo> propertiesVo = new ArrayList<PropertyTemplateVo>();
          for (StopsComponentProperty stopsComponentProperty : properties) {
            if (null == propertiesVo) {
              continue;
            }
            PropertyTemplateVo propertyTemplateVo = new PropertyTemplateVo();
            BeanUtils.copyProperties(stopsComponentProperty, propertyTemplateVo);
            propertiesVo.add(propertyTemplateVo);
          }
          stopsTemplateVo.setPropertiesVo(propertiesVo);
        }
        stopsTemplateVoList.add(stopsTemplateVo);
      }
      stopGroupVo.setStopsTemplateVoList(stopsTemplateVoList);

      stopGroupVoList.add(stopGroupVo);
    }
    return stopGroupVoList;
  }

  @Override
  public void updateGroupAndStopsListByServer(String username, String flowId) {

    String engineType = "";
    Flow flow = flowMapper.getFlowById(flowId);
    if (flow != null) {
      engineType = flow.getEngineType();
    }

    if (StringUtils.isEmpty(engineType)) {
      throw new IllegalArgumentException("engine type is empty");
    }

    Map<String, List<String>> stopsListWithGroup = stopImpl.getStopsListWithGroup(engineType);
    if (null == stopsListWithGroup || stopsListWithGroup.isEmpty()) {
      return;
    }

    // The call is successful, empty the "StopsComponentGroup" and "StopsComponent" message and insert
    stopsComponentDomain.deleteStopsComponentGroup(engineType);
    stopsComponentDomain.deleteStopsComponent(engineType);

    int addStopsComponentGroupRows = 0;

    // StopsComponent bundle list
    List<String> stopsBundleList = new ArrayList<>();

    // Loop stopsListWithGroup
    for (String groupName : stopsListWithGroup.keySet()) {
      if (StringUtils.isBlank(groupName)) {
        continue;
      }
      // add group info
      StopsComponentGroup stopsComponentGroup =
          StopsComponentGroupUtils.stopsComponentGroupNewNoId(username);
      stopsComponentGroup.setId(UUIDUtils.getUUID32());
      stopsComponentGroup.setGroupName(groupName);
      stopsComponentGroup.setEngineType(engineType);
      addStopsComponentGroupRows +=
          stopsComponentDomain.addStopsComponentGroup(stopsComponentGroup);
      // get current group stops bundle list
      List<String> list = stopsListWithGroup.get(groupName);
      stopsBundleList.addAll(list);
    }

    logger.debug("Successful insert Group" + addStopsComponentGroupRows + "piece of data!!!");

    // Determine if it is empty
    if (stopsBundleList.isEmpty()) {
      return;
    }

    // Deduplication
    HashSet<String> stopsBundleListDeduplication = new HashSet<>(stopsBundleList);
    stopsBundleList.clear();
    stopsBundleList.addAll(stopsBundleListDeduplication);

    int updateStopsComponentNum = 0;

    for (String bundle : stopsBundleList) {

      if (StringUtils.isBlank(bundle)) {
        continue;
      }

      // 2.First query "stopInfo" according to "bundle"
      logger.info("Now the call is：" + bundle);

      ThirdStopsComponentVo thirdStopsComponentVo = stopImpl.getStopInfo(bundle);
      if (null == thirdStopsComponentVo) {
        logger.warn("bundle:" + bundle + " is not data");
        continue;
      }
      List<String> stopGroupNameList = Arrays.asList(thirdStopsComponentVo.getGroups().split(Constants.COMMA()));
      // Query group information according to groupName in stops
      List<StopsComponentGroup> stopGroupByName = stopsComponentDomain.getStopGroupByNameList(stopGroupNameList, engineType);
      StopsComponent stopsComponent =
          StopsComponentUtils.thirdStopsComponentVoToStopsTemplate(
              username, thirdStopsComponentVo, stopGroupByName);

      if (null == stopsComponent) {
        continue;
      }

      stopsComponentDomain.addStopsComponentAndChildren(stopsComponent);

      logger.debug("=======association_groups_stops_template=====start=======");
      stopsComponentDomain.stopsComponentLinkStopsComponentGroupList(
          stopsComponent, stopsComponent.getStopGroupList());

      updateStopsComponentNum++;
    }

    logger.info("update StopsComponent Num :" + updateStopsComponentNum);
  }

  @Override
  public String stopsComponentList(String username, boolean isAdmin) {
    if (!isAdmin) {
      return ReturnMapUtils.setFailedMsgRtnJsonStr("Permission error");
    }
    List<StopsComponentGroupVo> stopGroupList = stopsComponentGroupDomain.getManageStopGroupList();
    for (StopsComponentGroupVo stopsComponentGroupVo : stopGroupList) {
      List<StopsComponentVo> stopsComponentVoList = stopsComponentGroupVo.getStopsComponentVoList();
      for (StopsComponentVo stopsComponentVo : stopsComponentVoList) {
        stopsComponentVo.setGroups(stopsComponentGroupVo.getGroupName());
      }
      stopsComponentGroupVo.setStopsComponentVoList(stopsComponentVoList);
    }
    return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("stopGroupList", stopGroupList);
  }
}
