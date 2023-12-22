package cn.cnic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author mayi
 */
@Configuration
public class LocalConfig {
  /** zn_CN or en. */
  @Value("{front_end.local:zn_CN}")
  private String local;

  /** dev or prod. */
  @Value("${front_end.center:dev}")
  private String center;

  public String getLocal() {
    return local;
  }

  public void setLocal(String local) {
    this.local = local;
  }

  public String getCenter() {
    return center;
  }

  public void setCenter(String center) {
    this.center = center;
  }
}
