package cn.cnic.component.system.mapper;

import cn.cnic.common.Eunm.ScheduleState;
import cn.cnic.component.system.entity.SysSchedule;
import cn.cnic.component.system.mapper.provider.SysScheduleMapperProvider;
import cn.cnic.component.system.vo.SysScheduleVo;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface SysScheduleMapper {

  @InsertProvider(type = SysScheduleMapperProvider.class, method = "insert")
  int insert(SysSchedule sysSchedule);

  @InsertProvider(type = SysScheduleMapperProvider.class, method = "update")
  int update(SysSchedule sysSchedule);

  @SelectProvider(type = SysScheduleMapperProvider.class, method = "getSysScheduleById")
  SysSchedule getSysScheduleById(boolean isAdmin, String id);

  /**
   * getSysScheduleListByStatus
   *
   * @param isAdmin isAdmin
   * @param status status
   */
  @SelectProvider(type = SysScheduleMapperProvider.class, method = "getSysScheduleListByStatus")
  List<SysSchedule> getSysScheduleListByStatus(boolean isAdmin, ScheduleState status);

  /**
   * getSysScheduleList
   *
   * @param param param
   */
  @SelectProvider(type = SysScheduleMapperProvider.class, method = "getSysScheduleList")
  List<SysScheduleVo> getSysScheduleList(boolean isAdmin, String param);

  @SelectProvider(type = SysScheduleMapperProvider.class, method = "getSysScheduleById")
  SysScheduleVo getSysScheduleVoById(boolean isAdmin, String id);

}
