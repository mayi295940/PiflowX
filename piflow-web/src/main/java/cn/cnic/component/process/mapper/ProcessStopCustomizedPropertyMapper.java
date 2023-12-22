package cn.cnic.component.process.mapper;

import cn.cnic.component.process.entity.ProcessStopCustomizedProperty;
import cn.cnic.component.process.mapper.provider.ProcessStopCustomizedPropertyMapperProvider;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.FetchType;

@Mapper
public interface ProcessStopCustomizedPropertyMapper {

  /**
   * Insert "list<ProcessStopCustomizedProperty>" Note that the method of spelling "sql" must use
   * "map" to connect the "Param" content to the key value.
   *
   * @param processStopCustomizedPropertyList (Content: "processStopCustomizedPropertyList" with a
   *     value of "List<ProcessStopCustomizedProperty>")
   */
  @InsertProvider(
      type = ProcessStopCustomizedPropertyMapperProvider.class,
      method = "addProcessStopCustomizedPropertyList")
  int addProcessStopCustomizedPropertyList(
      List<ProcessStopCustomizedProperty> processStopCustomizedPropertyList);

  @InsertProvider(
      type = ProcessStopCustomizedPropertyMapperProvider.class,
      method = "addProcessStopCustomizedProperty")
  int addProcessStopCustomizedProperty(ProcessStopCustomizedProperty processStopCustomizedProperty);

  @Select(
      "select * from process_stops_customized_property "
          + "where id = #{id} "
          + "and enable_flag = 1 ")
  @Results({
    @Result(
        column = "fk_flow_process_stop_id",
        property = "stops",
        many =
            @Many(
                select = "cn.cnic.component.flow.mapper.StopsMapper.getStopsById",
                fetchType = FetchType.LAZY))
  })
  ProcessStopCustomizedProperty getProcessStopCustomizedPropertyById(@Param("id") String id);

  @Select(
      "select * from process_stops_customized_property "
          + "where fk_flow_process_stop_id = #{processStopsId} "
          + "and enable_flag = 1 ")
  List<ProcessStopCustomizedProperty> getProcessStopCustomizedPropertyListByProcessStopsId(
      @Param("processStopsId") String processStopsId);

  @Select(
      "select * from process_stops_customized_property "
          + "where fk_flow_process_stop_id = #{processStopsId} "
          + "and name = #{name} "
          + "and enable_flag = 1 ")
  List<ProcessStopCustomizedProperty> getProcessStopCustomizedPropertyListByProcessStopsIdAndName(
      @Param("processStopsId") String processStopsId, @Param("name") String name);

  @UpdateProvider(
      type = ProcessStopCustomizedPropertyMapperProvider.class,
      method = "updateProcessStopCustomizedProperty")
  int updateProcessStopCustomizedProperty(
      ProcessStopCustomizedProperty processStopCustomizedProperty);

  @UpdateProvider(
      type = ProcessStopCustomizedPropertyMapperProvider.class,
      method = "updateEnableFlagByProcessStopId")
  int updateEnableFlagByProcessStopId(String username, String id);

  @UpdateProvider(
      type = ProcessStopCustomizedPropertyMapperProvider.class,
      method = "updateProcessStopCustomizedPropertyCustomValue")
  int updateProcessStopCustomizedPropertyCustomValue(String username, String content, String id);
}
