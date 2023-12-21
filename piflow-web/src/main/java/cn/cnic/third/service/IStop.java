package cn.cnic.third.service;

import cn.cnic.third.vo.stop.StopsHubVo;
import cn.cnic.third.vo.stop.ThirdStopsComponentVo;
import java.util.List;
import java.util.Map;

public interface IStop {

  /** Call the group interface */
  String[] getAllGroup();

  String[] getAllStops();

  Map<String, List<String>> getStopsListWithGroup(String engineType);

  ThirdStopsComponentVo getStopInfo(String bundleStr);

  String getStopsHubPath();

  StopsHubVo mountStopsHub(String stopsHubName);

  StopsHubVo unmountStopsHub(String stopsHubMountId);
}
