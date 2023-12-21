package cn.cnic.base.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {

  /** 创建API */
  @Bean
  public Docket createRestApi() {

    return new Docket(DocumentationType.SWAGGER_2)
        // 是否启用Swagger
        // 用来创建该API的基本信息，展示在文档的页面中（自定义展示的信息）
        .apiInfo(apiInfo())
        // 设置哪些接口暴露给Swagger展示
        .select()
        // 扫描所有有注解的api，用这种方式更灵活
        .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
        // 扫描指定包中的swagger注解
        // 扫描所有 .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build();
  }

  /** 添加摘要信息 */
  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("接口文档")
        .description("PiflowX-web接口文档")
        .version("版本号:1.0")
        .build();
  }
}
