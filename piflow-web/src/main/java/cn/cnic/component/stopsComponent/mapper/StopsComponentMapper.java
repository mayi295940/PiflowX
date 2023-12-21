package cn.cnic.component.stopsComponent.mapper;

import cn.cnic.component.stopsComponent.mapper.provider.StopsComponentMapperProvider;
import cn.cnic.component.stopsComponent.model.StopsComponent;
import cn.cnic.component.stopsComponent.vo.StopsComponentVo;
import java.util.List;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

@Mapper
public interface StopsComponentMapper {
  /**
   * Query all stops templates
   *
   * @return
   */
  @SelectProvider(type = StopsComponentMapperProvider.class, method = "getStopsComponentList")
  List<StopsComponent> getStopsComponentList();

  /**
   * Query template based on the stops template
   *
   * @param id
   * @return
   */
  @SelectProvider(type = StopsComponentMapperProvider.class, method = "getStopsComponentById")
  StopsComponent getStopsComponentById(String id);

  /**
   * Query the stops template based on the id of the stops template (including the attribute list)
   *
   * @param id
   * @return
   */
  @SelectProvider(type = StopsComponentMapperProvider.class, method = "getStopsComponentById")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        property = "properties",
        column = "id",
        many =
            @Many(
                select =
                    "cn.cnic.component.stopsComponent.mapper.StopsComponentPropertyMapper.getStopsComponentPropertyByStopsId"))
  })
  StopsComponent getStopsComponentAndPropertyById(String id);

  /**
   * Query the stops template according to the id of the stops group
   *
   * @param groupId
   * @return
   */
  @SelectProvider(
      type = StopsComponentMapperProvider.class,
      method = "getStopsComponentListByGroupId")
  List<StopsComponent> getStopsComponentListByGroupId(String groupId);

  /**
   * Query the stops template according to the id of the stops group
   *
   * @param groupId
   */
  @SelectProvider(
      type = StopsComponentMapperProvider.class,
      method = "getManageStopsComponentListByGroupId")
  List<StopsComponentVo> getManageStopsComponentListByGroupId(String groupId);

  /**
   * Query the stops template according to the id of the stops group...
   *
   * @param stopsName
   */
  @SelectProvider(type = StopsComponentMapperProvider.class, method = "getStopsComponentByName")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "properties",
        many =
            @Many(
                select =
                    "cn.cnic.component.stopsComponent.mapper.StopsComponentPropertyMapper.getStopsComponentPropertyByStopsId",
                fetchType = FetchType.LAZY))
  })
  List<StopsComponent> getStopsComponentByName(String stopsName);

  /**
   * Add more than one FLOW_STOPS_TEMPLATE.
   *
   * @param stopsComponent
   */
  @InsertProvider(type = StopsComponentMapperProvider.class, method = "insertStopsComponent")
  int insertStopsComponent(StopsComponent stopsComponent);

  /**
   * getStopsComponentByBundle
   *
   * @param bundle
   * @return
   */
  @Select("select fst.* from flow_stops_template fst where fst.bundle=#{bundle} and enable_flag=1")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "id",
        property = "properties",
        many =
            @Many(
                select =
                    "cn.cnic.component.stopsComponent.mapper.StopsComponentPropertyMapper.getStopsComponentPropertyByStopsId",
                fetchType = FetchType.LAZY))
  })
  StopsComponent getStopsComponentByBundle(String bundle);

  @Delete("delete from flow_stops_template where engine_type = #{engineType}")
  int deleteStopsComponent(String engineType);

  @Delete("delete from flow_stops_template where id = #{id}")
  int deleteStopsComponentById(String id);
}
