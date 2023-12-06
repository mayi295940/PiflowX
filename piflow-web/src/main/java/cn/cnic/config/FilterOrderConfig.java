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

package cn.cnic.config;

import cn.cnic.filter.Filter1AuthorizationFilter;
import cn.cnic.filter.UnFilterUrlFilter;
import com.webank.wedatasphere.dss.standard.app.sso.plugin.filter.SSOPluginFilter;
import javax.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * @author mayi
 */
@Configuration
public class FilterOrderConfig {

  private static final String APPLICATION_PATH = "/pipeline";

  @Bean
  public Filter filter1AuthorizationFilter() {
    return new Filter1AuthorizationFilter();
  }

  //  @Bean
  //  public Filter filter2TokenFilter() {
  //    return new Filter2TokenFilter();
  //  }

  @Bean
  public Filter unFilterUrlFilter() {
    return new UnFilterUrlFilter();
  }

  @Bean
  public FilterRegistrationBean<SSOPluginFilter> dssSSOFilter(
      @Autowired SSOPluginFilter ssoPluginFilter) {
    FilterRegistrationBean<SSOPluginFilter> filter = new FilterRegistrationBean<>();
    filter.setName("dssSSOFilter");
    filter.setFilter(ssoPluginFilter);
    // 指定优先级，顺序必须在第三方应用的用户登录判断Filter之前
    filter.setOrder(-9999);
    return filter;
  }

  @Bean
  public FilterRegistrationBean unFilterUrlFilterBean() {
    FilterRegistrationBean registration = new FilterRegistrationBean();
    registration.setName("unFilterUrlFilter");
    registration.setFilter(unFilterUrlFilter());
    registration.addUrlPatterns(APPLICATION_PATH + "/api/v1/*");
    registration.setOrder(0);
    return registration;
  }

  @Bean
  public FilterRegistrationBean characterEncodingFilter() {
    FilterRegistrationBean registration = new FilterRegistrationBean();
    registration.setName("characterEncodingFilter");
    registration.setFilter(new CharacterEncodingFilter("UTF-8"));
    registration.addUrlPatterns("/*");
    registration.setOrder(1);
    return registration;
  }

  @Bean
  public FilterRegistrationBean filterRegistrationBean1() {
    FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
    filterRegistrationBean.setName("filterRegistrationBean1");
    filterRegistrationBean.setFilter(filter1AuthorizationFilter());
    filterRegistrationBean.addUrlPatterns(APPLICATION_PATH + "/api/v1/*");
    filterRegistrationBean.setOrder(-9998);
    return filterRegistrationBean;
  }

}
