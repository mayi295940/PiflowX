package cn.cnic.component.dataSource.entity;

import cn.cnic.base.BaseModelUUIDNoCorpAgentId;
import cn.cnic.component.flow.entity.Stops;
import cn.cnic.component.stopsComponent.entity.StopsComponent;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DataSource extends BaseModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String dataSourceType;
  private String dataSourceName;
  private String dataSourceDescription;
  private Boolean isTemplate = false;
  private List<Stops> stopsList = new ArrayList<>();
  private List<DataSourceProperty> dataSourcePropertyList = new ArrayList<>();
  private String stopsTemplateBundle;
  private StopsComponent stopsComponent;
  private Boolean isAvailable = true;
  private String imageUrl;
}
