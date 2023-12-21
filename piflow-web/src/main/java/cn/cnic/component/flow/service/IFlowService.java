package cn.cnic.component.flow.service;

import cn.cnic.component.flow.entity.Flow;
import cn.cnic.component.flow.vo.FlowVo;
import java.util.List;
import javax.transaction.Transactional;

public interface IFlowService {

  /**
   * Query flow information based on id
   *
   * @param id
   * @return
   */
  @Transactional
  Flow getFlowById(String username, boolean isAdmin, String id);

  /**
   * Query flow information based on pageId
   *
   * @param fid
   * @param pageId
   * @return
   */
  @Transactional
  FlowVo getFlowByPageId(String fid, String pageId);

  /**
   * Query flow information based on id
   *
   * @param id
   * @return
   */
  @Transactional
  String getFlowVoById(String id);

  /**
   * add flow(Contains drawing board information)
   *
   * @param flowVo
   * @return
   */
  @Transactional
  String addFlow(String username, FlowVo flowVo);

  @Transactional
  String updateFlow(String username, Flow flow);

  @Transactional
  String deleteFLowInfo(String username, boolean isAdmin, String id);

  Integer getMaxStopPageId(String flowId);

  List<FlowVo> getFlowList();

  /**
   * Paging query flow
   *
   * @param username
   * @param isAdmin
   * @param offset Number of pages
   * @param limit Number of pages per page
   * @param param search for the keyword
   * @return
   */
  String getFlowListPage(
      String username, boolean isAdmin, Integer offset, Integer limit, String param);

  String getFlowExampleList();

  /**
   * Call the start interface and save the return information
   *
   * @param flowId
   * @return
   */
  String runFlow(String username, boolean isAdmin, String flowId, String runMode);

  String updateFlowBaseInfo(String username, String fId, FlowVo flowVo);

  String updateFlowNameById(
      String username, String id, String flowGroupId, String flowName, String pageId);

  Boolean updateFlowNameById(String username, String id, String flowName);

  Integer getMaxFlowPageIdByFlowGroupId(String flowGroupId);

  String drawingBoardData(String username, boolean isAdmin, String load, String parentAccessPath);
}
