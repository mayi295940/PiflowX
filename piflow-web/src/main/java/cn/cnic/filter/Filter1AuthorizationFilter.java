/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.cnic.filter;

import cn.cnic.component.system.entity.SysUser;
import cn.cnic.component.system.service.ISysUserService;
import cn.cnic.config.AuthFilterUrlConfig;
import cn.cnic.config.FrontEndConfig;
import cn.cnic.response.GeneralResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author mayi
 */
public class Filter1AuthorizationFilter implements Filter {

  @Autowired private FrontEndConfig frontEndConfig;
  @Autowired private AuthFilterUrlConfig authFilterUrlConfig;
  @Autowired private ISysUserService userService;

  private static final Logger LOGGER = LoggerFactory.getLogger(Filter1AuthorizationFilter.class);

  private List<String> permitUrlList = null;

  private static final String NOT_FILTER_METHOD = "OPTIONS";

  private ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void init(FilterConfig filterConfig) {
    permitUrlList = authFilterUrlConfig.getUnFilterUrls();
    if (permitUrlList == null) {
      permitUrlList = new ArrayList<>();
    }
  }

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) servletRequest;
    String requestUrl = request.getRequestURI();
    // Pass if file upload url accepted
    BodyReaderHttpServletRequestWrapper requestWrapper =
        new BodyReaderHttpServletRequestWrapper(request);
    printReceiveLog(requestWrapper);
    if (NOT_FILTER_METHOD.equals(requestWrapper.getMethod())) {
      filterChain.doFilter(requestWrapper, response);
      return;
    }
    HttpSession session = requestWrapper.getSession();
    if (!permitUrlList.contains(requestUrl)) {
      Object permissionObj = session.getAttribute("permissions");
      Object user = session.getAttribute("user");
      if (null == permissionObj || null == user) {
        String username = request.getRemoteUser();
        if (username == null) {
          // Redirect to login page
          LOGGER.info("Can not get username from sso, it will redirect to local login page");
          writeRedirectHome(response);
        } else {
          // 查询数据库，看用户是否存在
          SysUser userInDb = userService.findByUsername(username);
          if (userInDb != null) {
            // 放入session
            // loginService.addToSession(username, request);
            ((HttpServletResponse) response).sendRedirect(frontEndConfig.getHomePage());
          } else {
            // 自动创建用户
            LOGGER.warn("user: {}, do not exist, trying to create user", username);
            try {
              userService.autoAddUser(username);
              //              loginService.addToSession(username, request);
              ((HttpServletResponse) response).sendRedirect(frontEndConfig.getHomePage());
            } catch (Exception e) {
              LOGGER.error("Failed to auto add user, cause by: Failed to get role [PROJECTOR]", e);
            }
          }
        }
        return;
      }

      //      List<Permission> permissions = (List<Permission>) permissionObj;
      //      String method = requestWrapper.getMethod();
      //      if (!checkPermission(requestUrl, method, permissions)) {
      //        writeForbidden("no permissions", response);
      //        LOGGER.warn(
      //            "User: {} failed to access url: {}, caused by: No permissions",
      //            user,
      //            requestWrapper.getRequestURI());
      //        return;
      //      }
    }
    Object user = session.getAttribute("user");
    LOGGER.info("User: {} succeed to access url: {}", user, requestWrapper.getRequestURI());
    filterChain.doFilter(requestWrapper, response);
  }

  private void printReceiveLog(HttpServletRequest request) throws IOException {
    String requestUrl = request.getRequestURI();
    if (RequestMethod.GET.name().equalsIgnoreCase(request.getMethod())) {
      LOGGER.info("Receive request:[{}], method:[{}]", requestUrl, request.getMethod());
    } else {
      String body = CharStreams.toString(request.getReader());
      LOGGER.info(
          "Receive request:[{}], method:[{}], body:\n{}", requestUrl, request.getMethod(), body);
    }
  }

  private void writeRedirectHome(ServletResponse response) throws IOException {
    ((HttpServletResponse) response).setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
    ServletOutputStream out = response.getOutputStream();
    GeneralResponse generalResponse = new GeneralResponse<>("401", "please login", null);
    out.write(objectMapper.writeValueAsBytes(generalResponse));
    out.flush();
  }

  private void writeForbidden(String message, ServletResponse response) throws IOException {
    ((HttpServletResponse) response).setStatus(Response.Status.FORBIDDEN.getStatusCode());
    ServletOutputStream out = response.getOutputStream();
    GeneralResponse generalResponse = new GeneralResponse<>("403", message, null);
    out.write(objectMapper.writeValueAsBytes(generalResponse));
    out.flush();
  }

  /**
   * Return true if pass permissions, otherwise return false
   *
   * @param url
   * @param method
   * @param permissions
   * @return
   */
  //  private boolean checkPermission(String url, String method, List<Permission> permissions) {
  //    AntPathMatcher matcher = new AntPathMatcher();
  //    List<Permission> left =
  //        permissions.stream()
  //            .filter(
  //                (Permission p) -> matcher.match(p.getUrl(), url) &&
  // method.equals(p.getMethod()))
  //            .collect(Collectors.toList());
  //    return !left.isEmpty();
  //  }

  @Override
  public void destroy() {}
}
