/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.pipeline;

import com.webank.wedatasphere.dss.appconn.pipeline.execution.PipelineExecutionService;
import com.webank.wedatasphere.dss.appconn.pipeline.service.PipelineCrudService;
import com.webank.wedatasphere.dss.appconn.pipeline.service.PipelineQueryService;
import com.webank.wedatasphere.dss.appconn.pipeline.service.PipelineRefExportService;
import com.webank.wedatasphere.dss.appconn.pipeline.service.PipelineRefImportService;
import com.webank.wedatasphere.dss.standard.app.development.service.*;
import com.webank.wedatasphere.dss.standard.app.development.standard.AbstractDevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;

/**
 * @author mayi
 */
public class PipelineDevelopmentIntegrationStandard extends AbstractDevelopmentIntegrationStandard {

  @Override
  public void init() throws AppStandardErrorException {
    ssoRequestService.createSSORequestOperation(PipelineAppConn.PIPELINE_APPCONN_NAME);
    super.init();
  }

  @Override
  protected RefCRUDService createRefCRUDService() {
    return new PipelineCrudService();
  }

  @Override
  protected RefExecutionService createRefExecutionService() {
    return new PipelineExecutionService();
  }

  @Override
  protected RefExportService createRefExportService() {
    return new PipelineRefExportService();
  }

  @Override
  protected RefImportService createRefImportService() {
    return new PipelineRefImportService();
  }

  @Override
  protected RefQueryService createRefQueryService() {
    return new PipelineQueryService();
  }
}
