package com.webank.wedatasphere.dss.appconn.pipeline.utils;

import com.webank.wedatasphere.dss.appconn.pipeline.constant.PipelineProjectUserPermissionEnum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author allenzhou@webank.com
 * @date 2021/12/22 17:50
 */
public class AuthorizeUtil {
  public static List<Map<String, Object>> constructAuthorizeUsers(
      List<String> accessUsers, List<String> editUsers, List<String> releaseUsers) {
    if (CollectionUtils.isEmpty(accessUsers)
        && CollectionUtils.isEmpty(editUsers)
        && CollectionUtils.isEmpty(releaseUsers)) {
      return null;
    }
    List<Map<String, Object>> response = new ArrayList<>();
    List<Integer> releasePermission = new ArrayList<>(1);
    List<Integer> accessPermission = new ArrayList<>(1);
    List<Integer> editPermission = new ArrayList<>(2);
    releasePermission.add(PipelineProjectUserPermissionEnum.CREATOR.getCode());
    accessPermission.add(PipelineProjectUserPermissionEnum.BUSSMAN.getCode());
    editPermission.add(PipelineProjectUserPermissionEnum.DEVELOPER.getCode());
    editPermission.add(PipelineProjectUserPermissionEnum.OPERATOR.getCode());
    editPermission.add(PipelineProjectUserPermissionEnum.BUSSMAN.getCode());
    for (String user : releaseUsers) {
      Map<String, Object> userInfo = new HashMap<>(2);
      userInfo.put("project_user", user);
      userInfo.put("project_permissions", releasePermission);
      response.add(userInfo);
    }
    for (String user : editUsers) {
      if (releaseUsers.contains(user)) {
        continue;
      }
      Map<String, Object> userInfo = new HashMap<>(2);
      userInfo.put("project_user", user);
      userInfo.put("project_permissions", editPermission);
      response.add(userInfo);
    }
    for (String user : accessUsers) {
      if (releaseUsers.contains(user)) {
        continue;
      }
      if (editUsers.contains(user)) {
        continue;
      }
      Map<String, Object> userInfo = new HashMap<>(2);
      userInfo.put("project_user", user);
      userInfo.put("project_permissions", accessPermission);
      response.add(userInfo);
    }

    return response;
  }
}
