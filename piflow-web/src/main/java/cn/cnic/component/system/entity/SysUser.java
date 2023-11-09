package cn.cnic.component.system.entity;

import cn.cnic.base.BaseHibernateModelUUIDNoCorpAgentId;
import java.util.List;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Use JPA to define users. Implement the UserDetails interface, the user entity is the user used by
 * springSecurity.
 */
@Getter
@Setter
@Entity
@Table(name = "SYS_USER")
public class SysUser extends BaseHibernateModelUUIDNoCorpAgentId {

  private static final long serialVersionUID = 1L;

  private String username;

  private String password;

  private String name;

  private Integer age;

  private String sex;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "sysUser")
  private List<SysRole> roles;
}
