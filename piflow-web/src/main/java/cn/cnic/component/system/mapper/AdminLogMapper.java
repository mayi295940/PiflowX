package cn.cnic.component.system.mapper;

import cn.cnic.component.system.entity.SysLog;
import cn.cnic.component.system.mapper.provider.AdminLogMapperProvider;
import cn.cnic.component.system.vo.SysLogVo;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface AdminLogMapper {

  @SelectProvider(type = AdminLogMapperProvider.class, method = "getLogList")
  List<SysLogVo> getLogList(boolean isAdmin, String username, String param);

  @InsertProvider(type = AdminLogMapperProvider.class, method = "insertSelective")
  int insertSelective(SysLog record);
}
