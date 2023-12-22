package cn.cnic.component.stopsComponent.entity;

import cn.cnic.base.BaseModelUUIDNoCorpAgentId;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** Group name table */
@Setter
@Getter
public class StopsComponentGroup extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String groupName; // Group name

  private String engineType;

  // Group contains stop
  private List<StopsComponent> stopsComponentList = new ArrayList<>();
}
