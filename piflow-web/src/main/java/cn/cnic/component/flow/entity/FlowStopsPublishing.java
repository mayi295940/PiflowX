package cn.cnic.component.flow.entity;

import cn.cnic.base.BaseModelUUIDNoCorpAgentId;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/** stop component table */
@Getter
@Setter
public class FlowStopsPublishing extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String publishingId;
  private String name;
  private String state;
  private String flowId;
  private List<String> stopsIds;
}
