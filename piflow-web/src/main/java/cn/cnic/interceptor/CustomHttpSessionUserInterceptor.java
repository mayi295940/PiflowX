package cn.cnic.interceptor;

import cn.cnic.base.config.jwt.common.JwtUtils;
import cn.cnic.component.system.entity.SysUser;
import cn.cnic.component.system.service.ISysUserService;
import cn.cnic.utils.HttpUtils;
import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.HttpSessionUserInterceptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author mayi
 */
@Component
public class CustomHttpSessionUserInterceptor implements HttpSessionUserInterceptor {

  @Autowired private ISysUserService userService;

  @Autowired private JwtUtils jwtTokenUtil;

  private static final Logger LOGGER =
      LoggerFactory.getLogger(CustomHttpSessionUserInterceptor.class);

  @Override
  public void addUserToSession(String userName, HttpServletRequest request) {
    // 查询数据库，看用户是否存在
    SysUser sysUser = userService.findByUsername(userName);
    if (sysUser != null) {
      // 放入session
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

    Cookie cookie = new Cookie("token", jwtTokenUtil.getToken(userName));
    cookie.setSecure(true);
    cookie.setPath("/");

    Cookie jwtokCookie = new Cookie("state22", "jwtok");
    jwtokCookie.setSecure(true);
    jwtokCookie.setPath("/");

    HttpServletRequestWrapper requestWrapper =
        new HttpServletRequestWrapper(request) {
          @Override
          public Cookie[] getCookies() {
            List<Cookie> cookieList = new ArrayList<>();
            cookieList.add(cookie);
            cookieList.add(jwtokCookie);
            return (Cookie[]) cookieList.toArray();
          }
        };

    request = (HttpServletRequest) requestWrapper.getRequest();

    // 修改cookie
    ModifyHttpServletRequestWrapper mParametersWrapper = new ModifyHttpServletRequestWrapper(request);
    mParametersWrapper.putCookie("token222", jwtTokenUtil.getToken(userName));
    mParametersWrapper.putCookie("state", "jwtok");

    HttpSession session = request.getSession();

  }


  private class ModifyHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private Map<String, String> mapCookies;
    //将request对象中的参数修改后，放在这个集合里，随后项目取的所有Parameter都是从这个集合中取数
    private Map<String, String[]> params;
    private Map<String, String> headerMap;

    ModifyHttpServletRequestWrapper(HttpServletRequest request) {
      super(request);

      this.mapCookies = new HashMap<>();
      this.params = new HashMap<>();
      this.headerMap = new HashMap<>();
    }

    public void addHeader(String name, String value) {
      headerMap.put(name, value);
    }

    @Override
    public String getHeader(String name) {
      System.out.println("getHeader >>> " + name);
      String headerValue = super.getHeader(name);
      if (headerMap.containsKey(name)) {
        headerValue = headerMap.get(name);
      }
      return headerValue;
    }

    /**
     * get the Header names
     */
    @Override
    public Enumeration<String> getHeaderNames() {
      List<String> names = Collections.list(super.getHeaderNames());
      for (String name : headerMap.keySet()) {
        names.add(name);
      }
      return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
      System.out.println("getHeaders >>> " + name);
      List<String> values = Collections.list(super.getHeaders(name));
      if (headerMap.containsKey(name)) {
        values = Arrays.asList(headerMap.get(name));
      }
      return Collections.enumeration(values);
    }

    @Override
    public String getParameter(String name) {
      System.out.println("getParameter >>> " + name);
      String[] strings = params.get(name);
      return strings != null ? strings[0] : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
      System.out.println("getParameterMap >>> ");
      return params;
    }

    @Override
    public String[] getParameterValues(String name) {
      RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
      if (requestAttributes != null) {
        HttpServletRequest requestTmp = ((ServletRequestAttributes) requestAttributes).getRequest();
       // CookieUtils.getCookieValue(requestTmp, "");
      }
      System.out.println("getParameterValues >>> " + name);
      return params.get(name);
    }

    public void addParameter(String name, Object value) {
      if (value != null) {
        if (value instanceof String[]) {
          params.put(name, (String[]) value);
        } else if (value instanceof String) {
          params.put(name, new String[]{(String) value});
        } else {
          params.put(name, new String[]{String.valueOf(value)});
        }
      }
    }

    void putCookie(String name, String value) {
      this.mapCookies.put(name, value);
    }

    @Override
    public Cookie[] getCookies() {
      HttpServletRequest request = (HttpServletRequest) getRequest();
      Cookie[] cookies = request.getCookies();
      if (mapCookies == null || mapCookies.isEmpty()) {
        return cookies;
      }
      if (cookies == null || cookies.length == 0) {
        List<Cookie> cookieList = new LinkedList<>();
        for (Map.Entry<String, String> entry : mapCookies.entrySet()) {
          String key = entry.getKey();
          if (key != null && !"".equals(key)) {
            cookieList.add(new Cookie(key, entry.getValue()));
          }
        }
        if (cookieList.isEmpty()) {
          return cookies;
        }
        return cookieList.toArray(new Cookie[cookieList.size()]);
      } else {
        List<Cookie> cookieList = new ArrayList<>(Arrays.asList(cookies));
        for (Map.Entry<String, String> entry : mapCookies.entrySet()) {
          String key = entry.getKey();
          if (key != null && !"".equals(key)) {
            for (int i = 0; i < cookieList.size(); i++) {
              if (cookieList.get(i).getName().equals(key)) {
                cookieList.remove(i);
              }
            }
            cookieList.add(new Cookie(key, entry.getValue()));
          }
        }
        return cookieList.toArray(new Cookie[cookieList.size()]);
      }
    }
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
