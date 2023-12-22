package cn.cnic.base.config;

import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
// @Profile({"dev", "test"})// Set the "dev", "test" environment to open the "prod" environment and
// close it
public class Swagger2Config {
  @Bean
  public Docket createRestApi() {

    ParameterBuilder pb = new ParameterBuilder();
    pb.name("Authorization")
        .description("Token")
        .modelRef(new ModelRef("string"))
        .parameterType("header")
        .required(true)
        .defaultValue("Bearer ")
        .build();
    List<Parameter> par = new ArrayList<>();
    par.add(pb.build()); //
    return new Docket(DocumentationType.SWAGGER_2)
        .enable(true)
        .apiInfo(apiInfo())
        .groupName("piflow-web")
        .select()
        // Scan all annotated apis, which is more flexible
        .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
        // Scan the "swagger" annotation in the specified package
        // .apis(RequestHandlerSelectors.basePackage("cn.cnic.controller.api"))
        // Scan All
        // .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build()
        .globalOperationParameters(par);
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("web-api")
        .description("web-api")
        // .termsOfServiceUrl("http:/xxx/xxx")
        .version("1.0")
        .build();
  }
}
