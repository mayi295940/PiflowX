package cn.cnic.component.process.mapper;

import cn.cnic.component.process.entity.ProcessStopProperty;
import cn.cnic.component.process.mapper.provider.ProcessStopPropertyMapperProvider;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

@Mapper
public interface ProcessStopPropertyMapper {

  @InsertProvider(type = ProcessStopPropertyMapperProvider.class, method = "addProcessStopProperty")
  int addProcessStopProperty(ProcessStopProperty processStopProperty);

  @InsertProvider(
      type = ProcessStopPropertyMapperProvider.class,
      method = "addProcessStopProperties")
  int addProcessStopProperties(List<ProcessStopProperty> processStopPropertyList);

  /**
   * Query processStop attribute based on processStopId
   *
   * @param processStopId processStopId
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
  ProcessStopProperty getStopPropertyByProcessStopId(String processStopId);

  @UpdateProvider(
      type = ProcessStopPropertyMapperProvider.class,
      method = "updateProcessStopProperty")
  int updateProcessStopProperty(ProcessStopProperty processStopProperty);

  @UpdateProvider(
      type = ProcessStopPropertyMapperProvider.class,
      method = "updateEnableFlagByProcessStopId")
  int updateEnableFlagByProcessStopId(String processStopId, String username);
}
