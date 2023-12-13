package cn.cnic.component.template.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlowGroupTemplateVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private Date crtDttm;
  private String flowGroupName;
  private String name;
  private String description;
  private String path;
}