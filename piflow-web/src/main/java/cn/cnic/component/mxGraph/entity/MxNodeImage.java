package cn.cnic.component.mxGraph.entity;

import cn.cnic.base.BaseHibernateModelUUIDNoCorpAgentId;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "MX_NODE_IMAGE")
public class MxNodeImage extends BaseHibernateModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String imageName;
  private String imagePath;
  private String imageUrl;
  private String imageType;
}
