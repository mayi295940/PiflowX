package cn.cnic.base.config;

import cn.cnic.base.util.LoggerUtil;
import com.github.pagehelper.PageHelper;
import java.util.Properties;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Paging plug-in configuration */
@Configuration
public class MybatisConfig {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  Logger logger = LoggerUtil.getLogger();

  @Bean
  public PageHelper pageHelper() {
    logger.debug("...pageHelper...");
    PageHelper pageHelper = new PageHelper();
    Properties p = new Properties();
    p.setProperty("offsetAsPageNum", "true");
    p.setProperty("rowBoundsWithoutCount", "true");
    p.setProperty("reasonable", "true");
    pageHelper.setProperties(p);
    return pageHelper;
  }
}
