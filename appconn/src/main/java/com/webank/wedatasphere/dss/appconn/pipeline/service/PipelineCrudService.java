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

package com.webank.wedatasphere.dss.appconn.pipeline.service;

import com.webank.wedatasphere.dss.appconn.pipeline.ref.operation.PipelineRefCopyOperation;
import com.webank.wedatasphere.dss.appconn.pipeline.ref.operation.PipelineRefCreationOperation;
import com.webank.wedatasphere.dss.appconn.pipeline.ref.operation.PipelineRefDeletionOperation;
import com.webank.wedatasphere.dss.appconn.pipeline.ref.operation.PipelineRefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefCRUDService;

/**
 * @author mayi
 */
public class PipelineCrudService extends AbstractRefCRUDService {

  @Override
  public RefCreationOperation createRefCreationOperation() {
    return new PipelineRefCreationOperation();
  }

  @Override
  public RefCopyOperation createRefCopyOperation() {
    return new PipelineRefCopyOperation();
  }

  @Override
  public RefUpdateOperation createRefUpdateOperation() {
    return new PipelineRefUpdateOperation();
  }

  @Override
  public RefDeletionOperation createRefDeletionOperation() {
    return new PipelineRefDeletionOperation();
  }
}
