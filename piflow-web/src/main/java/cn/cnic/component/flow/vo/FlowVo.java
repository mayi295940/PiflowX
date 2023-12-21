package cn.cnic.component.flow.vo;

import cn.cnic.component.mxGraph.vo.MxGraphModelVo;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FlowVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  private String name;

  private String engineType;

  private String uuid;

  private String crtDttmString;

  private String description;

  private String driverMemory;

  private String executorNumber;

  private String executorMemory;

  private String executorCores;

  private Date crtDttm;

  private String pageId;

  private int stopQuantity;

  private MxGraphModelVo mxGraphModelVo; // Drawing board information

  private List<StopsVo> stopsVoList = new ArrayList<>(); // Current stream all stops

  private List<PathsVo> pathsVoList = new ArrayList<>(); // Current stream all paths
}
