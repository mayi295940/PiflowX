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
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef.CopyWitContextRequestRefImpl;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * @author mayi
 */
public class PipelineRefCopyOperation
    extends PipelineDevelopmentOperation<CopyWitContextRequestRefImpl, RefJobContentResponseRef>
    implements RefCopyOperation<ThirdlyRequestRef.CopyWitContextRequestRefImpl> {

  private static final String COPY_RULE_URL = "/pipeline/outer/api/v1/projector/rule/copy";
  private static final Logger LOGGER = LoggerFactory.getLogger(PipelineRefCopyOperation.class);

  private static String appId = "linkis_id";
  private static String appToken = "a33693de51";

  @Override
  protected String getAppConnName() {
    return PipelineAppConn.PIPELINE_APPCONN_NAME;
  }

  @Override
  public RefJobContentResponseRef copyRef(CopyWitContextRequestRefImpl requestRef)
      throws ExternalOperationFailedException {
    Gson gson = new Gson();
    LOGGER.info("Pipeline copy request: " + gson.toJson(requestRef));
    Map<String, Object> jobContent = requestRef.getRefJobContent();
    String url;
    try {
      url =
          HttpUtils.buildUrI(
                  getBaseUrl(),
                  COPY_RULE_URL,
                  appId,
                  appToken,
                  RandomStringUtils.randomNumeric(5),
                  String.valueOf(System.currentTimeMillis()))
              .toString();
    } catch (NoSuchAlgorithmException e) {
      LOGGER.error("Copy rule failed. Rule group info: {}, exception: {}", jobContent, e);
      throw new ExternalOperationFailedException(
          90156, "Construct copy outer url failed when copy.");
    } catch (URISyntaxException e) {
      LOGGER.error("Pipeline uri syntax exception.", e);
      throw new ExternalOperationFailedException(
          90156, "Construct copy outer url failed when copy.");
    }
    if (!jobContent.containsKey("ruleGroupId") || jobContent.get("ruleGroupId") == null) {
      throw new ExternalOperationFailedException(
          90156, "Rule group ID or username is null when copy.");
    }
    Integer ruleGroupId = null;
    if (jobContent.get("ruleGroupId") instanceof Double) {
      Double tempId = (Double) jobContent.get("ruleGroupId");
      ruleGroupId = tempId.intValue();
    } else if (jobContent.get("ruleGroupId") instanceof Integer) {
      ruleGroupId = (Integer) jobContent.get("ruleGroupId");
    }
    LOGGER.info("Rules in {} will be copied.", ruleGroupId.toString());
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      Map<String, Object> request = new HashMap<>(4);
      request.put("create_user", requestRef.getUserName());
      request.put("version", requestRef.getNewVersion());
      request.put("source_rule_group_id", Long.valueOf(ruleGroupId.toString()));
      request.put("target_project_id", requestRef.getParameter("projectId"));

      HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(request), headers);

      LOGGER.info(
          "Start to copy rule. url: {}, method: {}, body: {}",
          String.format("%s/%s", url, COPY_RULE_URL),
          javax.ws.rs.HttpMethod.POST,
          entity);

      RestTemplate restTemplate = new RestTemplate();
      Map<String, Object> response =
          restTemplate.exchange(url, HttpMethod.POST, entity, Map.class).getBody();
      if (response == null) {
        LOGGER.error("Failed to copy rule. Response is null.");
        throw new ExternalOperationFailedException(90156, "Copy outer url return null.");
      }
      LOGGER.info("Finished to copy rule. Response: {}", response);
      String status = response.get("code").toString();
      if (status != null && HttpStatus.OK.value() == Integer.parseInt(status)) {
        LOGGER.info("Copy rule successfully.");
      } else {
        LOGGER.error("Copy rule failed.");
        throw new ExternalOperationFailedException(
            90156, "Copy outer url return status is not ok.");
      }
      Map<String, Object> newJobContent = new HashMap<>(1);
      newJobContent.put(
          "ruleGroupId", ((Map<String, Object>) response.get("data")).get("rule_group_id"));
      return RefJobContentResponseRef.newBuilder().setRefJobContent(newJobContent).success();
    } catch (RestClientException e) {
      LOGGER.error("Failed to copy rule because of restTemplate exception. Exception is: {}", e);
      throw new ExternalOperationFailedException(
          90156, "Copy outer url request failed with rest template.");
    } catch (Exception e) {
      throw new ExternalOperationFailedException(90156, "Return pipeline copy response failed.");
    }
  }
}
