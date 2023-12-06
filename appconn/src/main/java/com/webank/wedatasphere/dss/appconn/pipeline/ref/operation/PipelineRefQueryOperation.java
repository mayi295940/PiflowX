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

import com.webank.wedatasphere.dss.appconn.pipeline.publish.PipelineDevelopmentOperation;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryJumpUrlOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.QueryJumpUrlResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef.QueryJumpUrlRequestRefImpl;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mayi
 */
public class PipelineRefQueryOperation
    extends PipelineDevelopmentOperation<QueryJumpUrlRequestRefImpl, QueryJumpUrlResponseRef>
    implements RefQueryJumpUrlOperation<QueryJumpUrlRequestRefImpl, QueryJumpUrlResponseRef> {

  private static final Logger LOGGER = LoggerFactory.getLogger(PipelineRefQueryOperation.class);

  private String getEnvUrl(
      String url, String host, int port, QueryJumpUrlRequestRefImpl queryJumpUrlRequestRef)
      throws UnsupportedEncodingException {

    Optional<EnvDSSLabel> labelOptional =
        queryJumpUrlRequestRef.getDSSLabels().stream()
            .filter(dssLabel -> dssLabel instanceof EnvDSSLabel)
            .map(dssLabel -> (EnvDSSLabel) dssLabel)
            .findAny();

    String env = "";
    if (labelOptional.isPresent()) {
      env = labelOptional.get().getEnv();
    }

    Long projectId = queryJumpUrlRequestRef.getRefProjectId();
    String redirectUrl =
        "http://"
            + host
            + ":"
             + port
            + "/#/drawingBoard?src=/drawingBoard/page/flow/mxGraph/index.html?load=582a4632b6f04cbe9eb28cba5762ccff&id="
            + projectId
            + "&nodeId=${nodeId}&contextID=${contextID}&nodeName=${nodeName}";

    return url
        + "?redirect="
        + URLEncoder.encode(redirectUrl + "&env=" + env.toLowerCase(), "UTF-8")
        + "&dssurl=${dssurl}&cookies=${cookies}";
  }

  @Override
  public QueryJumpUrlResponseRef query(QueryJumpUrlRequestRefImpl queryJumpUrlRequestRef)
      throws ExternalOperationFailedException {
    try {
      String baseUrl = getAppInstance().getBaseUrl() + "pipeline/api/v1/redirect";
      LOGGER.info("Get base url from app instance and return redirect url: " + baseUrl);
      URL url = new URL(baseUrl);
      String host = url.getHost();
      int port = url.getPort();
      String retJumpUrl = getEnvUrl(baseUrl, host, port, queryJumpUrlRequestRef);
      LOGGER.info("Get jump url from app instance: " + retJumpUrl);
      return QueryJumpUrlResponseRef.newBuilder().setJumpUrl(retJumpUrl).success();
    } catch (Exception e) {
      throw new ExternalOperationFailedException(90177, "Failed to parse jobContent ", e);
    }
  }
}
