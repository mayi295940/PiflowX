package cn.cnic.component.flow.entity;

import cn.cnic.base.BaseModelUUIDNoCorpAgentId;
import lombok.Getter;
import lombok.Setter;

/** stop property */
@Getter
@Setter
public class CustomizedProperty extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private Stops stops;
  private String name;
  private String customValue;
  private String description;
}
