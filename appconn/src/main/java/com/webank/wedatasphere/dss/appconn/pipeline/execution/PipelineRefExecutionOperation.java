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

package com.webank.wedatasphere.dss.appconn.pipeline.execution;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.pipeline.PipelineAppConn;
import com.webank.wedatasphere.dss.appconn.pipeline.constant.PipelineTaskStatusEnum;
import com.webank.wedatasphere.dss.appconn.pipeline.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.Killable;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.LongTermRefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.listener.core.Procedure;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.ExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.RefExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.HttpMethod;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
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
public class PipelineRefExecutionOperation
    extends LongTermRefExecutionOperation<
        RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef>
    implements Killable, Procedure {

  private static final String SUBMIT_TASK_PATH = "pipeline/outer/api/v1/execution";
  private static final String GET_TASK_STATUS_PATH =
      "pipeline/outer/api/v1/application/{applicationId}/status/";
  private static final String GET_TASK_RESULT_PATH =
      "pipeline/outer/api/v1/application/{applicationId}/result/";
  private static final String KILL_TASK_PATH =
      "pipeline/outer/api/v1/execution/application/kill/{applicationId}/{executionUser}";
  private static final String NODE_NAME_KEY = "nodeName";
  private static final String RULEGROUPID = "ruleGroupId";
  private static final String RULE_GROUP_ID = "rule_group_id";
  private static final String EXECUTION_USER_KEY = "executionUser";
  private static final String WDS_SUBMIT_USER_KEY = "wds.dss.workflow.submit.user";

  private static final Logger LOGGER = LoggerFactory.getLogger(PipelineRefExecutionOperation.class);

  private String appId = "linkis_id";
  private String appToken = "a33693de51";

  private static final String FILTER = "filter";

  @Override
  protected String getAppConnName() {
    return PipelineAppConn.PIPELINE_APPCONN_NAME;
  }

  @Override
  public boolean kill(RefExecutionAction action) {

    String applicationId = ((PipelineRefExecutionAction) action).getApplicationId();
    String executionUser = ((PipelineRefExecutionAction) action).getExecutionUser();

    if (applicationId == null) {
      LOGGER.error(
          "Cannot get application id from PipelineNodeExecutionAction. Kill pipeline job failed.");
      return false;
    }

    try {
      // Send request and get response
      RestTemplate restTemplate = new RestTemplate();
      HttpHeaders headers = new HttpHeaders();
      HttpEntity entity = new HttpEntity(headers);

      String path =
          KILL_TASK_PATH
              .replace("{applicationId}", applicationId)
              .replace("{executionUser}", executionUser);
      URI url =
          HttpUtils.buildUrI(
              getBaseUrl(),
              path,
              appId,
              appToken,
              RandomStringUtils.randomNumeric(5),
              String.valueOf(System.currentTimeMillis()));
      String startLog =
          String.format(
              "Start to kill job. url: %s, method: %s, body: %s", url, HttpMethod.GET, entity);
      LOGGER.info(startLog);
      Map<String, Object> response = restTemplate.getForEntity(url.toString(), Map.class).getBody();

      if (response == null) {
        String errorMsg =
            String.format(
                "Error! Can not get kill result, job_id: %s, response is null", applicationId);
        LOGGER.error(errorMsg);
        return false;
      }

      if (!checkResponse(response)) {
        String message = (String) response.get("message");
        String errorMsg = String.format("Error! Can not get kill result, exception: %s", message);
        LOGGER.error(errorMsg);
        return false;
      }

      String finishLog = String.format("Succeed to get kill result. response: %s", response);
      LOGGER.info(finishLog);
      return true;
    } catch (Exception e) {
      String errorMsg = String.format("Error! Can not kill job result, job_id: %s", applicationId);
      LOGGER.error(errorMsg, e);
      return false;
    }
  }

  @Override
  protected RefExecutionAction submit(RefExecutionProjectWithContextRequestRef requestRef)
      throws ExternalOperationFailedException {
    try {
      Map<String, Object> jobContent = requestRef.getRefJobContent();
      Map<String, Object> runtimeMap = requestRef.getExecutionRequestRefContext().getRuntimeMap();
      LOGGER.info("pipeline rule group content: " + new Gson().toJson(jobContent));
      String executionUser = String.valueOf(runtimeMap.get(WDS_SUBMIT_USER_KEY).toString());
      String realExecutionUser =
          String.valueOf(
              runtimeMap.get(EXECUTION_USER_KEY) != null
                  ? runtimeMap.get(EXECUTION_USER_KEY).toString()
                  : "");
      String nodeName = String.valueOf(runtimeMap.get(NODE_NAME_KEY));
      if (StringUtils.isEmpty(nodeName)) {
        nodeName = requestRef.getName();
      }
      LOGGER.info("The node name: " + nodeName);
      String id = "";
      if (jobContent.get(RULEGROUPID) != null) {
        id = jobContent.get(RULEGROUPID).toString();
      } else {
        id = jobContent.get(RULE_GROUP_ID).toString();
      }

      float f = Float.parseFloat(id);

      Long groupId = (long) f;

      if (nodeName == null) {
        String errorMsg = "Error! Can not submit job, node name is null";
        LOGGER.error(errorMsg);
        return null;
      }
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      Gson gson = new Gson();

      Map<String, Object> requestPayLoad = new HashMap<>();
      requestPayLoad.put(
          "execution_user",
          StringUtils.isNotBlank(realExecutionUser) ? realExecutionUser : executionUser);
      requestPayLoad.put("create_user", executionUser);
      requestPayLoad.put("node_name", nodeName);
      requestPayLoad.put("group_id", groupId);

      LOGGER.info(
          "The execution user: "
              + (StringUtils.isNotBlank(realExecutionUser) ? realExecutionUser : executionUser));
      // Get parameters.
      LOGGER.info("The execution request: " + runtimeMap);

      StringBuffer executionParam = new StringBuffer();

      getVariables(requestRef, runtimeMap, requestPayLoad, executionParam);

      HttpEntity<Object> entity = new HttpEntity<>(gson.toJson(requestPayLoad), headers);

      RestTemplate restTemplate = new RestTemplate();
      String url =
          HttpUtils.buildUrI(
                  getBaseUrl(),
                  SUBMIT_TASK_PATH,
                  appId,
                  appToken,
                  RandomStringUtils.randomNumeric(5),
                  String.valueOf(System.currentTimeMillis()))
              .toString();
      LOGGER.info(
          "Start to submit job to linkis. url: {}, method: {}, body: {}",
          url,
          javax.ws.rs.HttpMethod.POST,
          entity);
      Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);
      String finishLog = String.format("Succeed to submit job to pipeline. response: %s", response);
      LOGGER.info(finishLog);

      if (response == null) {
        String errorMsg = "Error! Can not submit job, response is null";
        LOGGER.error(errorMsg);
        return null;
      }

      if (!checkResponse(response)) {
        String message = (String) response.get("message");
        String errorMsg = String.format("Error! Can not submit job, exception: %s", message);
        LOGGER.error(errorMsg);
        return null;
      }

      String applicationId =
          (String) ((Map<String, Object>) response.get("data")).get("application_id");
      LOGGER.info("pipeline application ID: {}", applicationId);
      return new PipelineRefExecutionAction(applicationId, executionUser);
    } catch (Exception e) {
      String errorMsg = "Error! Can not submit job";
      LOGGER.error(errorMsg, e);
      return null;
    }
  }

  private void getVariables(
      RefExecutionProjectWithContextRequestRef requestRef,
      Map<String, Object> runtimeMap,
      Map<String, Object> requestPayLoad,
      StringBuffer executionParam) {
    if (requestRef.getVariables() != null) {
      for (String currentKey : requestRef.getVariables().keySet()) {
        executionParam
            .append(currentKey)
            .append(":")
            .append(requestRef.getVariables().get(currentKey).toString())
            .append(";");
      }
    }

    if (runtimeMap.get(FILTER) != null) {
      String userFilter = String.valueOf(runtimeMap.get(FILTER).toString());
      LOGGER.info("The execution filter: " + userFilter);

      if (StringUtils.isNotBlank(userFilter)) {
        executionParam.append(userFilter);
      }
    }

    requestPayLoad.put("execution_param", executionParam.toString());
    LOGGER.info("The global variable is: " + executionParam.toString());
  }

  private Boolean checkResponse(Map<String, Object> response) {
    String responseStatus = (String) response.get("code");
    return HttpStatus.OK.value() == Integer.parseInt(responseStatus);
  }

  @Override
  public RefExecutionState state(RefExecutionAction action) {
    if (null == action) {
      return RefExecutionState.Failed;
    }

    PipelineRefExecutionAction pipelineRefExecutionAction = (PipelineRefExecutionAction) action;
    String applicationId = pipelineRefExecutionAction.getApplicationId();
    String executionUser = pipelineRefExecutionAction.getExecutionUser();
    LOGGER.info("pipeline application ID: {}", applicationId);
    LOGGER.info("pipeline execution user: {}", executionUser);
    if (StringUtils.isEmpty(applicationId) || StringUtils.isEmpty(executionUser)) {
      return RefExecutionState.Failed;
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Object> entity = new HttpEntity<>(headers);

    RestTemplate restTemplate = new RestTemplate();
    String url = null;
    try {
      url =
          HttpUtils.buildUrI(
                  getBaseUrl(),
                  GET_TASK_STATUS_PATH.replace("{applicationId}", applicationId),
                  appId,
                  appToken,
                  RandomStringUtils.randomNumeric(5),
                  String.valueOf(System.currentTimeMillis()))
              .toString();
    } catch (NoSuchAlgorithmException e) {
      LOGGER.info("pipeline no signature algor.", e);
    } catch (URISyntaxException e) {
      LOGGER.error("pipeline uri syntax exception.", e);
    }
    LOGGER.info("Start to check job. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
    Map<String, Object> response = restTemplate.getForEntity(url, Map.class).getBody();
    String finishLog = String.format("Succeed to submit job to pipeline. response: %s", response);
    LOGGER.info(finishLog);

    if (response == null) {
      String errorMsg = "Error! Can not check job, response is null";
      LOGGER.error(errorMsg);
      return null;
    }

    if (!checkResponse(response)) {
      String message = (String) response.get("message");
      String errorMsg = String.format("Error! Can not submit job, exception: %s", message);
      LOGGER.error(errorMsg);
      return null;
    }

    LOGGER.info("Succeed to get job status. response: {}", response);
    List<Map<String, Object>> tasks =
        (List<Map<String, Object>>) ((Map<String, Object>) response.get("data")).get("task");
    Map<RefExecutionState, Integer> statusCountMap = new HashMap<RefExecutionState, Integer>(8);
    initCountMap(statusCountMap);
    Integer taskSize = tasks.size();
    for (Map<String, Object> task : tasks) {
      Integer taskStatus = (Integer) task.get("task_status");
      Boolean abortOnFailure = (Boolean) task.get("abort_on_failure");
      addStatus(taskStatus, abortOnFailure, statusCountMap);
    }

    Integer runningCount = statusCountMap.get(RefExecutionState.Running);
    Integer successCount = statusCountMap.get(RefExecutionState.Success);
    Integer failedCount = statusCountMap.get(RefExecutionState.Failed);

    if (runningCount != 0) {
      return RefExecutionState.Running;
    } else if (successCount.equals(taskSize) && tasks.size() != 0) {
      return RefExecutionState.Success;
    } else if (failedCount != 0) {
      return RefExecutionState.Failed;
    } else {
      return RefExecutionState.Accepted;
    }
  }

  private void addStatus(
      Integer status, Boolean abortOnFailure, Map<RefExecutionState, Integer> statusCountMap) {
    if (status.equals(PipelineTaskStatusEnum.SUBMITTED.getCode())) {
      statusCountMap.put(
          RefExecutionState.Accepted, statusCountMap.get(RefExecutionState.Accepted) + 1);
    } else if (status.equals(PipelineTaskStatusEnum.INITED.getCode())) {
      statusCountMap.put(
          RefExecutionState.Accepted, statusCountMap.get(RefExecutionState.Accepted) + 1);
    } else if (status.equals(PipelineTaskStatusEnum.RUNNING.getCode())) {
      statusCountMap.put(
          RefExecutionState.Running, statusCountMap.get(RefExecutionState.Running) + 1);
    } else if (status.equals(PipelineTaskStatusEnum.SUCCEED.getCode())) {
      statusCountMap.put(
          RefExecutionState.Success, statusCountMap.get(RefExecutionState.Success) + 1);
    } else if (status.equals(PipelineTaskStatusEnum.PASS_CHECKOUT.getCode())) {
      statusCountMap.put(
          RefExecutionState.Success, statusCountMap.get(RefExecutionState.Success) + 1);
    } else if (status.equals(PipelineTaskStatusEnum.FAIL_CHECKOUT.getCode())) {
      if (abortOnFailure != null && abortOnFailure) {
        statusCountMap.put(
            RefExecutionState.Failed, statusCountMap.get(RefExecutionState.Failed) + 1);
      } else {
        statusCountMap.put(
            RefExecutionState.Success, statusCountMap.get(RefExecutionState.Success) + 1);
      }
    } else if (status.equals(PipelineTaskStatusEnum.FAILED.getCode())) {
      statusCountMap.put(
          RefExecutionState.Failed, statusCountMap.get(RefExecutionState.Failed) + 1);
    } else if (status.equals(PipelineTaskStatusEnum.TASK_NOT_EXIST.getCode())) {
      statusCountMap.put(
          RefExecutionState.Failed, statusCountMap.get(RefExecutionState.Failed) + 1);
    } else if (status.equals(PipelineTaskStatusEnum.CANCELLED.getCode())) {
      statusCountMap.put(
          RefExecutionState.Killed, statusCountMap.get(RefExecutionState.Killed) + 1);
    } else if (status.equals(PipelineTaskStatusEnum.TIMEOUT.getCode())) {
      statusCountMap.put(
          RefExecutionState.Failed, statusCountMap.get(RefExecutionState.Failed) + 1);
    } else if (status.equals(PipelineTaskStatusEnum.SCHEDULED.getCode())) {
      statusCountMap.put(
          RefExecutionState.Accepted, statusCountMap.get(RefExecutionState.Accepted) + 1);
    }
  }

  private void initCountMap(Map<RefExecutionState, Integer> statusCountMap) {
    statusCountMap.put(RefExecutionState.Accepted, 0);
    statusCountMap.put(RefExecutionState.Running, 0);
    statusCountMap.put(RefExecutionState.Success, 0);
    statusCountMap.put(RefExecutionState.Failed, 0);
  }

  @Override
  public ExecutionResponseRef result(RefExecutionAction action) {
    if (null == action) {
      return ExecutionResponseRef.newBuilder().error();
    }

    PipelineRefExecutionAction pipelineRefExecutionAction = (PipelineRefExecutionAction) action;
    String applicationId = pipelineRefExecutionAction.getApplicationId();

    if (StringUtils.isEmpty(applicationId)) {
      return ExecutionResponseRef.newBuilder().error();
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Object> entity = new HttpEntity<>(headers);

    RestTemplate restTemplate = new RestTemplate();
    String url = null;
    try {
      url =
          HttpUtils.buildUrI(
                  getBaseUrl(),
                  GET_TASK_RESULT_PATH.replace("{applicationId}", applicationId),
                  appId,
                  appToken,
                  RandomStringUtils.randomNumeric(5),
                  String.valueOf(System.currentTimeMillis()))
              .toString();
    } catch (NoSuchAlgorithmException e) {
      LOGGER.info("pipeline no signature algor.", e);
    } catch (URISyntaxException e) {
      LOGGER.error("pipeline uri syntax exception.", e);
    }
    LOGGER.info(
        "Start to get job result. url: {}, method: {}, body: {}", url, HttpMethod.GET, entity);
    Map<String, Object> response = restTemplate.getForEntity(url, Map.class).getBody();
    String finishLog = String.format("Succeed to submit job to pipeline. response: %s", response);
    LOGGER.info(finishLog);

    if (response == null) {
      String errorMsg = "Error! Can not check job, response is null";
      LOGGER.error(errorMsg);
      return null;
    }

    if (!checkResponse(response)) {
      String message = (String) response.get("message");
      String errorMsg = String.format("Error! Can not submit job, exception: %s", message);
      LOGGER.error(errorMsg);
      return null;
    }

    LOGGER.info("Succeed to get job result. response: {}", response);
    Integer passNum = (Integer) ((Map<String, Object>) response.get("data")).get("pass_num");
    Integer failedNum = (Integer) ((Map<String, Object>) response.get("data")).get("failed_num");
    Integer notPassNum = (Integer) ((Map<String, Object>) response.get("data")).get("not_pass_num");
    String resultMessage =
        (String) ((Map<String, Object>) response.get("data")).get("result_message");

    String taskMsg =
        String.format(
            "Task result: Pass/Failed/Not Pass ------- %s/%s/%s", passNum, failedNum, notPassNum);
    LOGGER.info(taskMsg);
    LOGGER.info(resultMessage);

    if (failedNum != 0) {
      return ExecutionResponseRef.newBuilder().error();
    } else {
      return ExecutionResponseRef.newBuilder().success();
    }
  }

  @Override
  public float progress(RefExecutionAction action) {
    return 0.5f;
  }

  @Override
  public String log(RefExecutionAction action) {
    return "";
  }
}
