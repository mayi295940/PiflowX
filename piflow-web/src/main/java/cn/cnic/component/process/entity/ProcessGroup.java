package cn.cnic.component.process.entity;

import cn.cnic.base.BaseModelUUIDNoCorpAgentId;
import cn.cnic.common.Eunm.ProcessParentType;
import cn.cnic.common.Eunm.ProcessState;
import cn.cnic.common.Eunm.RunModeType;
import cn.cnic.component.mxGraph.entity.MxGraphModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessGroup extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String name;
  private String viewXml;
  private String description;
  private String pageId;
  private String flowId;
  private String appId;
  private String parentProcessId;
  private String processId;
  private ProcessState state;
  private Date startTime;
  private Date endTime;
  private String progress;
  private RunModeType runModeType = RunModeType.RUN;
  private ProcessParentType processParentType;
  private ProcessGroup processGroup;
  private MxGraphModel mxGraphModel;
  private List<Process> processList = new ArrayList<>();
  private List<ProcessGroupPath> processGroupPathList = new ArrayList<>();
  private List<ProcessGroup> processGroupList = new ArrayList<>();
}
