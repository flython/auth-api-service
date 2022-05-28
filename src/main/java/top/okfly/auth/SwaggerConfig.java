package top.okfly.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.service.ParameterType;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket docket() {
        Docket docket = new Docket(DocumentationType.OAS_30)
                .apiInfo(new ApiInfoBuilder()
                        .title("auth-api-doc")
                        .description("description")
                        .version("1.0")
                        .build())
                .groupName("all")
                .select()
                .apis(RequestHandlerSelectors.basePackage("top.okfly.auth.portal.controller"))
                .paths(PathSelectors.any())
                .build();

        RequestParameterBuilder builder = new RequestParameterBuilder();
        docket.globalRequestParameters(List.of(
                builder.name("Authorization").in(ParameterType.HEADER).required(false).build()
        ));
        return docket;


    }

}
