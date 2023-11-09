package cn.cnic.component.dashboard.service.impl;

import cn.cnic.component.dashboard.service.IResourceService;
import cn.cnic.third.service.IResource;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ResourceServiceImpl implements IResourceService {

  @Resource private IResource resourceImpl;

  @Override
  public String getResourceInfo() {
    String resourceInfo = resourceImpl.getResourceInfo();
    return resourceInfo;
  }
}
