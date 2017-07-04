package ch.littlecafet.server;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.base.Predicates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
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

@SpringBootApplication
@EnableAutoConfiguration
@EnableSwagger2
@ComponentScan
@Import({springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration.class})
public class LittlecafetApplication {

	public static void main(String[] args) {
		SpringApplication.run(LittlecafetApplication.class, args);
	}

	private static final Logger log = LoggerFactory.getLogger(LittlecafetApplication.class);

    @Bean
    public Docket EasyPayApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("EasyPay")
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
                .tags(new Tag("EasyPay Service", "All apis relating to EasyPay"));

    }


    @Autowired
    private TypeResolver typeResolver;


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("EasyPay API")
                .description("Système de paiement mobile")
                .termsOfServiceUrl("http://springfox.io")
                .license("Apache License Version 2.0")
                .licenseUrl("https://github.com/springfox/springfox/blob/master/LICENSE")
                .version("0.1")
                .build();
    }

/*	@Bean
	public CommandLineRunner demo(UserRepository repository) {
		return (args) -> {

			repository.save(new User("Jack", "Bauer", "test1", ""));
			repository.save(new User("Chloe", "O'Brian", "test2",""));
			repository.save(new User("Kim", "Bauer", "test3",""));
			repository.save(new User("David", "Palmer", "test4",""));
			repository.save(new User("Michelle", "Dessler", "test5",""));


			// fetch all customers
			log.info("Users found with findAll():");
			log.info("-------------------------------");
			for (User user : repository.findAll()) {
				log.info(user.toString());
			}
			log.info("");
		};
	}*/
}
