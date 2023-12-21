package cn.cnic.controller;

import cn.cnic.base.util.ReturnMapUtils;
import cn.cnic.base.util.SessionUserUtil;
import cn.cnic.component.flow.service.ICustomizedPropertyService;
import cn.cnic.component.flow.service.IPropertyService;
import cn.cnic.component.flow.service.IStopsService;
import cn.cnic.component.flow.vo.StopsCustomizedPropertyVo;
import cn.cnic.component.stopsComponent.service.IStopGroupService;
import cn.cnic.component.stopsComponent.service.IStopsHubService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "stops")
@RestController
@RequestMapping("/stops")
public class StopsCtrl {

  @Resource private IStopGroupService stopGroupServiceImpl;

  @Resource private IPropertyService propertyServiceImpl;

  @Resource private IStopsService stopsServiceImpl;

  @Resource private ICustomizedPropertyService customizedPropertyServiceImpl;

  @Resource private IStopsHubService stopsHubServiceImpl;

  /**
   * 'stops' and 'groups' on the left of' reload'
   *
   * @param load flow id
   */
  @ApiOperation("reloadStops")
  @RequestMapping("/reloadStops")
  @ResponseBody
  public String reloadStops(String load) {
    String username = SessionUserUtil.getCurrentUsername();
    stopGroupServiceImpl.updateGroupAndStopsListByServer(username, load);
    return ReturnMapUtils.setSucceededCustomParamRtnJsonStr("load", load);
  }

  @ApiOperation("queryIdInfo")
  @RequestMapping("/queryIdInfo")
  @ResponseBody
  public String getStopGroup(String fid, String stopPageId) {
    return propertyServiceImpl.queryAll(fid, stopPageId);
  }

  @ApiOperation("deleteLastReloadData")
  @RequestMapping("/deleteLastReloadData")
  @ResponseBody
  public String deleteLastReloadData(String stopId) {
    return propertyServiceImpl.deleteLastReloadDataByStopsId(stopId);
  }

  /**
   * Get the usage of the current connection port
   *
   * @param request request
   */
  @ApiOperation("getStopsPort")
  @RequestMapping("/getStopsPort")
  @ResponseBody
  public String getStopsPort(HttpServletRequest request) {
    // Take parameters
    // flowId
    String flowId = request.getParameter("flowId");
    // PageId of output's stop
    String sourceId = request.getParameter("sourceId");
    // PageId of input stop
    String targetId = request.getParameter("targetId");
    // ID of path
    String pathLineId = request.getParameter("pathLineId");
    return stopsServiceImpl.getStopsPort(flowId, sourceId, targetId, pathLineId);
  }

  /**
   * Multiple saves to modify
   *
   * @param content content
   * @param id id
   */
  @ApiOperation("updateStops")
  @RequestMapping("/updateStops")
  @ResponseBody
  public String updateStops(HttpServletRequest request, String[] content, String id) {
    String username = SessionUserUtil.getCurrentUsername();
    return propertyServiceImpl.updatePropertyList(username, content);
  }

  @ApiOperation("updateStopsOne")
  @RequestMapping("/updateStopsOne")
  @ResponseBody
  public String updateStops(HttpServletRequest request, String content, String id) {
    String username = SessionUserUtil.getCurrentUsername();
    return propertyServiceImpl.updateProperty(username, content, id);
  }

  @ApiOperation("updateStopsById")
  @RequestMapping("/updateStopsById")
  @ResponseBody
  public String updateStopsById(HttpServletRequest request) throws Exception {
    String id = request.getParameter("stopId");
    String isCheckpointStr = request.getParameter("isCheckpoint");
    String username = SessionUserUtil.getCurrentUsername();
    return stopsServiceImpl.updateStopsCheckpoint(username, id, isCheckpointStr);
  }

  @ApiOperation("updateStopsNameById")
  @RequestMapping("/updateStopsNameById")
  @ResponseBody
  public String updateStopsNameById(HttpServletRequest request) throws Exception {
    String id = request.getParameter("stopId");
    String flowId = request.getParameter("flowId");
    String stopName = request.getParameter("name");
    String pageId = request.getParameter("pageId");
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    return stopsServiceImpl.updateStopName(username, isAdmin, id, flowId, stopName, pageId);
  }

  @ApiOperation("addStopCustomizedProperty")
  @RequestMapping("/addStopCustomizedProperty")
  @ResponseBody
  public String addStopCustomizedProperty(StopsCustomizedPropertyVo stopsCustomizedPropertyVo) {
    String username = SessionUserUtil.getCurrentUsername();
    return customizedPropertyServiceImpl.addStopCustomizedProperty(
        username, stopsCustomizedPropertyVo);
  }

  @ApiOperation("updateStopsCustomizedProperty")
  @RequestMapping("/updateStopsCustomizedProperty")
  @ResponseBody
  public String updateStopsCustomizedProperty(StopsCustomizedPropertyVo stopsCustomizedPropertyVo) {
    String username = SessionUserUtil.getCurrentUsername();
    return customizedPropertyServiceImpl.updateStopsCustomizedProperty(
        username, stopsCustomizedPropertyVo);
  }

  @ApiOperation("deleteStopsCustomizedProperty")
  @RequestMapping("/deleteStopsCustomizedProperty")
  @ResponseBody
  public String deleteStopsCustomizedProperty(String customPropertyId) {
    String username = SessionUserUtil.getCurrentUsername();
    return customizedPropertyServiceImpl.deleteStopsCustomizedProperty(username, customPropertyId);
  }

  @ApiOperation("deleteRouterStopsCustomizedProperty")
  @RequestMapping("/deleteRouterStopsCustomizedProperty")
  @ResponseBody
  public String deleteRouterStopsCustomizedProperty(String customPropertyId) {
    String username = SessionUserUtil.getCurrentUsername();
    return customizedPropertyServiceImpl.deleteRouterStopsCustomizedProperty(
        username, customPropertyId);
  }

  @ApiOperation("getRouterStopsCustomizedProperty")
  @RequestMapping("/getRouterStopsCustomizedProperty")
  @ResponseBody
  public String getRouterStopsCustomizedProperty(String customPropertyId) {
    return customizedPropertyServiceImpl.getRouterStopsCustomizedProperty(customPropertyId);
  }

  /**
   * Query and enter the process list
   *
   * @param page page
   * @param limit limit
   * @param param param
   */
  @ApiOperation("stopsHubListPage")
  @RequestMapping("/stopsHubListPage")
  @ResponseBody
  public String stopsHubListPage(Integer page, Integer limit, String param) {
    String username = SessionUserUtil.getCurrentUsername();
    boolean isAdmin = SessionUserUtil.isAdmin();
    return stopsHubServiceImpl.stopsHubListPage(username, isAdmin, page, limit, param);
  }

  /**
   * Upload stopsHub jar file and save stopsHub
   *
   * @param file file
   */
  @ApiOperation("uploadStopsHubFile")
  @RequestMapping(value = "/uploadStopsHubFile", method = RequestMethod.POST)
  @ResponseBody
  public String uploadStopsHubFile(@RequestParam("file") MultipartFile file) {
    String username = SessionUserUtil.getCurrentUsername();
    return stopsHubServiceImpl.uploadStopsHubFile(username, file);
  }

  /**
   * Mount stopsHub
   *
   * @param id id
   */
  @ApiOperation("mountStopsHub")
  @RequestMapping(value = "/mountStopsHub", method = RequestMethod.POST)
  @ResponseBody
  public String mountStopsHub(HttpServletRequest request, String id) {
    String username = SessionUserUtil.getCurrentUsername();
    Boolean isAdmin = SessionUserUtil.isAdmin();
    return stopsHubServiceImpl.mountStopsHub(username, isAdmin, id);
  }

  /**
   * unmount stopsHub
   *
   * @param id id
   */
  @ApiOperation("unmountStopsHub")
  @RequestMapping(value = "/unmountStopsHub", method = RequestMethod.POST)
  @ResponseBody
  public String unmountStopsHub(HttpServletRequest request, String id) {
    String username = SessionUserUtil.getCurrentUsername();
    Boolean isAdmin = SessionUserUtil.isAdmin();
    return stopsHubServiceImpl.unmountStopsHub(username, isAdmin, id);
  }

  /**
   * unmount stopsHub
   *
   * @param id id
   */
  @ApiOperation("delStopsHub")
  @RequestMapping(value = "/delStopsHub", method = RequestMethod.POST)
  @ResponseBody
  public String delStopsHub(HttpServletRequest request, String id) {
    String username = SessionUserUtil.getCurrentUsername();
    Boolean isAdmin = SessionUserUtil.isAdmin();
    return stopsHubServiceImpl.delStopsHub(username, isAdmin, id);
  }
}
