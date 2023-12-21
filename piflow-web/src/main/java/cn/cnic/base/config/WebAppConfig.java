package cn.cnic.base.config;

import cn.cnic.base.util.LoggerUtil;
import java.util.Arrays;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName: WebAppConfig @Description: TODO(Here is a sentence describing the function of this
 * class.)
 */
@Configuration
public class WebAppConfig implements WebMvcConfigurer {
  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  Logger logger = LoggerUtil.getLogger();

  /** Inject the first defined interceptor */
  @Autowired private ConfigInterceptor configInterceptor;

  // Method of accessing pictures and videos
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {

    String storagePathHead = System.getProperty("user.dir");

    String imagesPathFlink = ("file:" + storagePathHead + "/storage/flink/image/");
    String videosPathFlink = ("file:" + storagePathHead + "/storage/flink/video/");
    String xmlPathFlink = ("file:" + storagePathHead + "/storage/flink/xml/");

    logger.info("imagesPathFlink=" + imagesPathFlink);
    logger.info("videosPathFlink=" + videosPathFlink);
    logger.info("xmlPathFlink=" + xmlPathFlink);

    String imagesPathSpark = ("file:" + storagePathHead + "/storage/spark/image/");
    String videosPathSpar = ("file:" + storagePathHead + "/storage/spark/video/");
    String xmlPathSpar = ("file:" + storagePathHead + "/storage/spark/xml/");

    logger.info("imagesPathSpark=" + imagesPathSpark);
    logger.info("videosPathSpar=" + videosPathSpar);
    logger.info("xmlPathSpar=" + xmlPathSpar);

    registry
        .addResourceHandler("/images/**", "/videos/**", "/xml/**")
        .addResourceLocations(
            imagesPathFlink,
            videosPathFlink,
            xmlPathFlink,
            imagesPathSpark,
            videosPathSpar,
            xmlPathSpar);

    // Swagger2Config
    registry
        .addResourceHandler("/**")
        .addResourceLocations("classpath:/META-INF/resources/")
        .addResourceLocations("classpath:/resources/")
        .addResourceLocations("classpath:/static/")
        .addResourceLocations("classpath:/public/");

    WebMvcConfigurer.super.addResourceHandlers(registry);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(configInterceptor)
        .excludePathPatterns(
            Arrays.asList("/components/**", "/js/**", "/css/**", "/img/**", "/img/*"));
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedOrigins("*")
        .allowedMethods("GET", "POST", "OPTIONS", "PUT", "DELETE")
        .allowedHeaders("*")
        .allowCredentials(false)
        .maxAge(3600);
    WebMvcConfigurer.super.addCorsMappings(registry);
  }
}
