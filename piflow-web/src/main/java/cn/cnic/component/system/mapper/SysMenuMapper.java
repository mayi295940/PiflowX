package cn.cnic.component.system.mapper;

import cn.cnic.component.system.entity.SysMenu;
import cn.cnic.component.system.mapper.provider.SysMenuMapperProvider;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface SysMenuMapper {

  /**
   * getSysMenuList
   *
   * @param role
   */
  @SelectProvider(type = SysMenuMapperProvider.class, method = "getSysMenuList")
  public List<SysMenu> getSysMenuList(String role);

  @SelectProvider(type = SysMenuMapperProvider.class, method = "getSampleMenuList")
  public List<SysMenu> getSampleMenuList();

  @SelectProvider(type = SysMenuMapperProvider.class, method = "deleteSampleMenuListByIds")
  public List<SysMenu> deleteSampleMenuListByIds(@Param("ids") String[] ids);
}