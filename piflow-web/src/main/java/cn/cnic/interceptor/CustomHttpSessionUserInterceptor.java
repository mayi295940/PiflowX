package cn.cnic.interceptor;

import cn.cnic.component.system.entity.SysUser;
import cn.cnic.component.system.service.ISysUserService;
import cn.cnic.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.HttpSessionUserInterceptor;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author mayi
 */
@Component
public class CustomHttpSessionUserInterceptor implements HttpSessionUserInterceptor {

  @Autowired private ISysUserService userService;

  private static final Logger LOGGER =
      LoggerFactory.getLogger(CustomHttpSessionUserInterceptor.class);

  @Override
  public void addUserToSession(String userName, HttpServletRequest request) {
    // 查询数据库，看用户是否存在
    SysUser sysUser = userService.findByUsername(userName);
    if (sysUser != null) {
      // 放入session
      // loginService.addToSession(userName, request);
      userService.jwtLogin(userName, userName);
      LOGGER.info("User: {} succeed to login", userName);
    } else {
      // 自动创建用户
      LOGGER.warn("user: {}, do not exist, trying to create user", userName);
      try {
        userService.autoAddUser(userName);
        userService.jwtLogin(userName, userName);
      } catch (Exception e) {
        LOGGER.error("Failed to auto add user, cause by: Failed to get role [PROJECTOR]", e);
      }
    }
    HttpSession session = request.getSession();
    Cookie cookie = new Cookie("JSESSIONID", session.getId());
    cookie.setSecure(true);
    cookie.setPath("/");
    HttpServletRequestWrapper requestWrapper =
        new HttpServletRequestWrapper(request) {
          @Override
          public Cookie[] getCookies() {
            List<Cookie> cookieList = new ArrayList<>();
            cookieList.add(cookie);
            final Cookie[] cookies = (Cookie[]) cookieList.toArray();
            return cookies;
          }
        };

    request = (HttpServletRequest) requestWrapper.getRequest();
  }

  @Override
  public boolean isUserExistInSession(HttpServletRequest httpServletRequest) {
    HttpSession session = httpServletRequest.getSession();
    Object permissionObj = session.getAttribute("permissions");
    Object user = session.getAttribute("user");
    return null != permissionObj && null != user;
  }

  @Override
  public String getUser(HttpServletRequest httpServletRequest) {
    return HttpUtils.getUserName(httpServletRequest);
  }
}
