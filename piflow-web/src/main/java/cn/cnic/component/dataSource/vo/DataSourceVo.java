package cn.cnic.component.dataSource.vo;

import cn.cnic.component.stopsComponent.entity.StopsComponent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataSourceVo implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;
  private String dataSourceType;
  private String dataSourceName;
  private String dataSourceDescription;
  private Boolean isTemplate = false;

  private List<DataSourcePropertyVo> dataSourcePropertyVoList = new ArrayList<>();
  private String stopsTemplateBundle;
  private StopsComponent stopsComponent;
  private String stopsName;
  private Boolean isAvailable;
  private String imageUrl;
}
