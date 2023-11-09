package cn.cnic.component.process.mapper;

import cn.cnic.component.process.entity.ProcessStopProperty;
import cn.cnic.component.process.mapper.provider.ProcessStopPropertyMapperProvider;
import java.util.List;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ProcessStopPropertyMapper {

  @InsertProvider(type = ProcessStopPropertyMapperProvider.class, method = "addProcessStopProperty")
  public int addProcessStopProperty(ProcessStopProperty processStopProperty);

  @InsertProvider(
      type = ProcessStopPropertyMapperProvider.class,
      method = "addProcessStopProperties")
  public int addProcessStopProperties(
      @Param("processStopPropertyList") List<ProcessStopProperty> processStopPropertyList);

  /**
   * Query processStop attribute based on processStopId
   *
   * @param processStopId
   * @return
   */
  @SelectProvider(
      type = ProcessStopPropertyMapperProvider.class,
      method = "getStopPropertyByProcessStopId")
  @Results({
    @Result(column = "custom_value", property = "customValue"),
    @Result(column = "allowable_values", property = "allowableValues"),
    @Result(column = "property_required", property = "required"),
    @Result(column = "property_sensitive", property = "sensitive")
  })
  public ProcessStopProperty getStopPropertyByProcessStopId(String processStopId);

  @UpdateProvider(
      type = ProcessStopPropertyMapperProvider.class,
      method = "updateProcessStopProperty")
  public int updateProcessStopProperty(ProcessStopProperty processStopProperty);

  @UpdateProvider(
      type = ProcessStopPropertyMapperProvider.class,
      method = "updateEnableFlagByProcessStopId")
  public int updateEnableFlagByProcessStopId(String processStopId, String username);
}
