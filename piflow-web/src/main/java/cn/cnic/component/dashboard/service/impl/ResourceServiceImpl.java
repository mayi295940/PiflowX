package cn.cnic.component.dashboard.service.impl;

import cn.cnic.component.dashboard.service.IResourceService;
import cn.cnic.third.service.IResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceServiceImpl implements IResourceService {

  private final IResource resourceImpl;

  @Autowired
  public ResourceServiceImpl(IResource resourceImpl) {
    this.resourceImpl = resourceImpl;
  }

  @Override
  public String getResourceInfo() {
    String resourceInfo = resourceImpl.getResourceInfo();
    return resourceInfo;
  }
}
