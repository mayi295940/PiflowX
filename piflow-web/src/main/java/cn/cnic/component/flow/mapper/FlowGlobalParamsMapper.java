package cn.cnic.component.flow.mapper;

import cn.cnic.component.flow.entity.FlowGlobalParams;
import cn.cnic.component.flow.mapper.provider.FlowGlobalParamsMapperProvider;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

@Mapper
public interface FlowGlobalParamsMapper {

  @InsertProvider(type = FlowGlobalParamsMapperProvider.class, method = "addGlobalParams")
  int addGlobalParams(FlowGlobalParams globalParams);

  @UpdateProvider(type = FlowGlobalParamsMapperProvider.class, method = "updateGlobalParams")
  int updateGlobalParams(FlowGlobalParams globalParams);

  @UpdateProvider(type = FlowGlobalParamsMapperProvider.class, method = "updateEnableFlagById")
  int updateEnableFlagById(String username, String id, boolean enableFlag);

  @SelectProvider(type = FlowGlobalParamsMapperProvider.class, method = "getGlobalParamsListParam")
  List<FlowGlobalParams> getGlobalParamsListParam(String username, boolean isAdmin, String param);

  /**
   * Query FlowGlobalParams based on FlowGroup Id
   *
   * @param id FlowGroup Id
   */
  @SelectProvider(type = FlowGlobalParamsMapperProvider.class, method = "getGlobalParamsById")
  FlowGlobalParams getGlobalParamsById(String username, boolean isAdmin, String id);

  @SelectProvider(type = FlowGlobalParamsMapperProvider.class, method = "getFlowGlobalParamsByIds")
  List<FlowGlobalParams> getFlowGlobalParamsByIds(String[] ids);

  @SelectProvider(
      type = FlowGlobalParamsMapperProvider.class,
      method = "getFlowGlobalParamsByFlowId")
  List<FlowGlobalParams> getFlowGlobalParamsByFlowId(String flowId);

  @SelectProvider(
      type = FlowGlobalParamsMapperProvider.class,
      method = "getFlowGlobalParamsByProcessId")
  List<FlowGlobalParams> getFlowGlobalParamsByProcessId(String processId);

}