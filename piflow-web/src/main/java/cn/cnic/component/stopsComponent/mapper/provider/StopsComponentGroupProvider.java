package cn.cnic.component.stopsComponent.mapper.provider;

import cn.cnic.base.util.SqlUtils;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

public class StopsComponentGroupProvider {

  /** 查詢所有組 */
  public String getStopGroupList(@Param("engineType") String engineType) {
    String sqlStr = "";
    SQL sql = new SQL();
    sql.SELECT("*");
    sql.FROM("flow_stops_groups");
    sql.WHERE("enable_flag = 1 and engine_type = '" + engineType + "'");
    sql.ORDER_BY(" group_name ");
    sqlStr = sql.toString();
    return sqlStr;
  }

  public String getStopGroupByGroupNameList(
      @Param("group_name") List<String> groupName, @Param("engineType") String engineType) {
    return "select * from flow_stops_groups where group_name in ("
        + SqlUtils.strListToStr(groupName)
        + ") and enable_flag = 1 and engine_type = '"
        + engineType
        + "'";
  }
}
