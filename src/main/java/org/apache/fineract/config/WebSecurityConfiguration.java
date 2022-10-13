package org.apache.fineract.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.method.HandlerMethod;


@OpenAPIDefinition(
        info = @Info(
                title = "Operations App",
                description = "" +
                        "Operations app is a secure, multi-tenanted microservice platform",
//                contact = @Contact(
//                        name = "Operations",
//                        url =
////                        email =
//                )
                license = @License(
                        name = "MIT Licence",
                        url = "https://github.com/openMF/ph-ee-operations-app/blob/master/LICENSE"))
)
//@SecurityScheme(
//        name = "api",
//        scheme = "bearer",
//        type = SecuritySchemeType.HTTP,
//        in = SecuritySchemeIn.HEADER,
//        paramName = "Platform-TenantId"
//)

@SecurityScheme(
        name = "Bearer Token",
        scheme = "apikey",
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "Bearer"
)

//@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/swagger-ui/**",
                "/swagger-config/**");

    }

    @Bean
    public OperationCustomizer customGlobalHeaders() {

        return (Operation operation, HandlerMethod handlerMethod) -> {

            Parameter missingParam1 = new HeaderParameter()
                    .in(ParameterIn.HEADER.toString())
                    .name("Platform-TenantId")
                    .schema(new StringSchema())
                    .description("Tenant Id")
                    .example("ibank-india")
                    .required(true);
            operation.addParametersItem(missingParam1);
            return operation;
        };
    }
}