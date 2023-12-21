package cn.cnic.controller;

import cn.cnic.base.util.SessionUserUtil;
import cn.cnic.component.flow.entity.Flow;
import cn.cnic.component.flow.service.IFlowService;
import cn.cnic.component.flow.vo.FlowVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(tags = "flow")
@Controller
@RequestMapping("/flow")
public class FlowCtrl {

  @Autowired private IFlowService flowServiceImpl;

  /**
   * flowList page query
   *
   * @param page page
   * @param limit limit
   * @param param param
   */
  @ApiOperation("getFlowListPage")
  @RequestMapping("/getFlowListPage")
  @ResponseBody
  public String getFlowListPage(Integer page, Integer limit, String param) {
    boolean isAdmin = SessionUserUtil.isAdmin();
    String username = SessionUserUtil.getCurrentUsername();
    return flowServiceImpl.getFlowListPage(username, isAdmin, page, limit, param);
  }

  /** Enter the front page of the drawing board */
  @ApiOperation("drawingBoardData")
  @RequestMapping("/drawingBoardData")
  @ResponseBody
  public String drawingBoardData(String load, String parentAccessPath) {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    return flowServiceImpl.drawingBoardData(username, isAdmin, load, parentAccessPath);
  }

  /** run Flow */
  @ApiOperation("runFlow")
  @RequestMapping("/runFlow")
  @ResponseBody
  public String runFlow(String flowId, String runMode) {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    return flowServiceImpl.runFlow(username, isAdmin, flowId, runMode);
  }

  @ApiOperation("queryFlowData")
  @RequestMapping("/queryFlowData")
  @ResponseBody
  public String queryFlowData(String load) {
    return flowServiceImpl.getFlowVoById(load);
  }

  /**
   * save flow
   *
   * @param flowVo flow vo
   */
  @ApiOperation("saveFlowInfo")
  @RequestMapping("/saveFlowInfo")
  @ResponseBody
  public String saveFlowInfo(FlowVo flowVo) {
    String username = SessionUserUtil.getCurrentUsername();
    return flowServiceImpl.addFlow(username, flowVo);
  }

  /**
   * update Flow
   *
   * @param flow flow bean
   */
  @ApiOperation("updateFlowInfo")
  @RequestMapping("/updateFlowInfo")
  @ResponseBody
  public String updateFlowInfo(Flow flow) {
    String username = SessionUserUtil.getCurrentUsername();
    return flowServiceImpl.updateFlow(username, flow);
  }

  /**
   * Delete flow association information according to flowId
   *
   * @param id flow id
   */
  @ApiOperation("deleteFlow")
  @RequestMapping("/deleteFlow")
  @ResponseBody
  public String deleteFlow(String id) {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    return flowServiceImpl.deleteFLowInfo(username, isAdmin, id);
  }

  @ApiOperation("updateFlowBaseInfo")
  @RequestMapping("/updateFlowBaseInfo")
  @ResponseBody
  public String updateFlowBaseInfo(String fId, FlowVo flowVo) {
    String username = SessionUserUtil.getCurrentUsername();
    return flowServiceImpl.updateFlowBaseInfo(username, fId, flowVo);
  }
}
