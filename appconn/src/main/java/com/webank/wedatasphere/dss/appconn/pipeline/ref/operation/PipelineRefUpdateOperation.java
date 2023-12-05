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

package com.webank.wedatasphere.dss.appconn.pipeline.ref.operation;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.pipeline.PipelineAppConn;
import com.webank.wedatasphere.dss.appconn.pipeline.publish.PipelineDevelopmentOperation;
import com.webank.wedatasphere.dss.appconn.pipeline.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef.UpdateWitContextRequestRefImpl;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * @author mayi
 */
public class PipelineRefUpdateOperation
    extends PipelineDevelopmentOperation<UpdateWitContextRequestRefImpl, ResponseRef>
    implements RefUpdateOperation<ThirdlyRequestRef.UpdateWitContextRequestRefImpl> {

  private static final String UPDATE_RULE_PATH = "/pipeline/outer/api/v1/projector/rule/modify";

  private String appId = "linkis_id";
  private String appToken = "a33693de51";

  private static final Logger LOGGER = LoggerFactory.getLogger(PipelineRefUpdateOperation.class);

  @Override
  protected String getAppConnName() {
    return PipelineAppConn.PIPELINE_APPCONN_NAME;
  }

  private ResponseRef updatePipelineCS(UpdateWitContextRequestRefImpl updateWitContextRequestRef)
      throws ExternalOperationFailedException {
    LOGGER.info("Update CS request body" + new Gson().toJson(updateWitContextRequestRef));
    String csId = updateWitContextRequestRef.getContextId();
    String projectName = updateWitContextRequestRef.getProjectName();

    LOGGER.info("Start set context for pipeline node: {}", updateWitContextRequestRef.getName());
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    Map<String, Object> requestPayLoad = new HashMap<>();
    try {
      requestPayLoad.put("cs_id", csId);
      requestPayLoad.put("rule_name", updateWitContextRequestRef.getName());
      requestPayLoad.put("project_name", projectName);
      requestPayLoad.put("project_id", updateWitContextRequestRef.getRefProjectId());

      RestTemplate restTemplate = new RestTemplate();

      HttpEntity<Object> entity = new HttpEntity<>(new Gson().toJson(requestPayLoad), headers);
      String url =
          HttpUtils.buildUrI(
                  getBaseUrl(),
                  UPDATE_RULE_PATH,
                  appId,
                  appToken,
                  RandomStringUtils.randomNumeric(5),
                  String.valueOf(System.currentTimeMillis()))
              .toString();
      LOGGER.info("Set context service url is {}", url);
      Map<String, Object> response =
          restTemplate
              .exchange(url, org.springframework.http.HttpMethod.POST, entity, Map.class)
              .getBody();
      if (response == null) {
        LOGGER.error("Failed to delete rule. Response is null.");
        throw new ExternalOperationFailedException(
            90176, "Failed to delete rule. Response is null.");
      }
      String status = response.get("code").toString();
      if (!"200".equals(status)) {
        String errorMsg = response.get("message").toString();
        throw new ExternalOperationFailedException(90176, errorMsg);
      }
      return new ResponseRefImpl(new Gson().toJson(response), HttpStatus.OK.value(), "", response);
    } catch (Exception e) {
      throw new ExternalOperationFailedException(
          90176, "Set context pipeline AppJointNode Exception", e);
    }
  }

  @Override
  public ResponseRef updateRef(UpdateWitContextRequestRefImpl requestRef)
      throws ExternalOperationFailedException {
    return updatePipelineCS(requestRef);
  }
}
