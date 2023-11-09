package cn.cnic.component.mxGraph.service.impl;

import cn.cnic.base.util.LoggerUtil;
import cn.cnic.component.mxGraph.mapper.MxGeometryMapper;
import cn.cnic.component.mxGraph.service.IMxGraphService;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class MxGraphServiceImpl implements IMxGraphService {

  Logger logger = LoggerUtil.getLogger();

  @Resource private MxGeometryMapper mxGeometryMapper;

  @Override
  public int deleteMxGraphById(String username, String id) {
    return mxGeometryMapper.updateEnableFlagById(username, id);
  }
}
