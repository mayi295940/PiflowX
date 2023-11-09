package cn.cnic.component.system.mapper;

import cn.cnic.component.system.entity.SysRole;
import cn.cnic.component.system.mapper.provider.SysRoleMapperProvider;
import java.util.List;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysRoleMapper {

  @Select("select Max(id) from sys_role")
  public long getMaxId();

  /**
   * getSysRoleListBySysUserId
   *
   * @param sysUserId
   * @return
   */
  @SelectProvider(type = SysRoleMapperProvider.class, method = "getSysRoleListBySysUserId")
  public List<SysRole> getSysRoleListBySysUserId(@Param("sysUserId") String sysUserId);

  @InsertProvider(type = SysRoleMapperProvider.class, method = "insertSysRoleList")
  public int insertSysRoleList(@Param("userId") String userId, @Param("roles") List<SysRole> roles);
}
