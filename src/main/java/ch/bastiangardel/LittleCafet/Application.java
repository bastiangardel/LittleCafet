package ch.bastiangardel.LittleCafet;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;

import static springfox.documentation.schema.AlternateTypeRules.newRule;



@EnableAutoConfiguration
@EnableSwagger2
@ComponentScan
@EnableJpaAuditing
@Import({springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration.class})
public class Application {

    public static void main(String... args) {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                (hostname, sslSession) -> {
                    if (hostname.equals("localhost")) {
                        return true;
                    }
                    return false;
                });




        new SpringApplicationBuilder()
                .sources(Application.class,ShiroConfiguration.class)
                .run(args);
    }

    @Bean
    public Docket LittleCafetApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("LittleCafet")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/")
                .directModelSubstitute(LocalDate.class,
                        String.class)
                .genericModelSubstitutes(ResponseEntity.class)
                .alternateTypeRules(
                        newRule(typeResolver.resolve(DeferredResult.class,
                                typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                                typeResolver.resolve(WildcardType.class)))
                .useDefaultResponseMessages(false)
                .enableUrlTemplating(false)
                .tags(new Tag("LittleCafet Service", "All apis relating to LittleCafet"));

    }


    @Autowired
    private TypeResolver typeResolver;


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("LittleCafet")
                .description("Gestion of a little Cafeteria")
                .termsOfServiceUrl("http://springfox.io")
                .license("The MIT License")
                .licenseUrl("https://opensource.org/licenses/MIT")
                .version("0.1")
                .build();
    }

}
