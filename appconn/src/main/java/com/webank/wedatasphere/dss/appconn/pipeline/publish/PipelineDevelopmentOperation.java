package com.webank.wedatasphere.dss.appconn.pipeline.publish;

import com.webank.wedatasphere.dss.appconn.pipeline.PipelineAppConn;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.DevelopmentRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;

/**
 * @author mayi
 */
public class PipelineDevelopmentOperation<K extends DevelopmentRequestRef<K>, V extends ResponseRef>
    extends AbstractDevelopmentOperation<K, V> {

  /**
   * I override this method, since I want to use SSORequestOperation to request Visualis server.
   *
   * @return visualis appConn name
   */
  @Override
  protected String getAppConnName() {
    return PipelineAppConn.PIPELINE_APPCONN_NAME;
  }

  protected DevelopmentService getDevelopmentService() {
    return (DevelopmentService) service;
  }

  protected AppInstance getAppInstance() {
    return service.getAppInstance();
  }
}
