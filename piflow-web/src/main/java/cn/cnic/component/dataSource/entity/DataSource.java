package cn.cnic.component.dataSource.entity;

import cn.cnic.base.BaseHibernateModelUUIDNoCorpAgentId;
import cn.cnic.component.flow.entity.Stops;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OrderBy;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "DATA_SOURCE")
@Setter
@Getter
public class DataSource extends BaseHibernateModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  @Column(columnDefinition = "varchar(255) COMMENT 'dataSourceType'")
  private String dataSourceType;

  @Column(columnDefinition = "varchar(255) COMMENT 'dataSourceName'")
  private String dataSourceName;

  @Column(columnDefinition = "text(0) COMMENT 'dataSourceDescription'")
  private String dataSourceDescription;

  @Column(columnDefinition = "bit(1) COMMENT 'isTemplate'")
  private Boolean isTemplate = false;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "dataSource")
  @Where(clause = "enable_flag=1")
  @OrderBy(clause = "lastUpdateDttm desc")
  private List<Stops> stopsList = new ArrayList<>();

  @OneToMany(fetch = FetchType.EAGER, mappedBy = "dataSource")
  @Where(clause = "enable_flag=1")
  @OrderBy(clause = "lastUpdateDttm desc")
  private List<DataSourceProperty> dataSourcePropertyList = new ArrayList<>();
}
