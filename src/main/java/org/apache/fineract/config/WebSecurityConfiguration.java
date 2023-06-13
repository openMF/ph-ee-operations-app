package org.apache.fineract.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.HandlerMethod;

//@OpenAPIDefinition (
//        info = @Info (
//                title = "Operations App",
//                description = "" +
//                        "Operations app is a secure, multi-tenanted microservice platform",
//                license = @License (
//                        name = "MIT Licence",
//                        url = "https://github.com/openMF/ph-ee-operations-app/blob/master/LICENSE"
//                )
//        )
//)
//@SecurityScheme (
//        name = "auth",
//        scheme = "bearer",
//        type = SecuritySchemeType.HTTP,
//        in = SecuritySchemeIn.HEADER,
//        paramName = "Authorization: Bearer",
//        description = "Use this curl request to generate authToken\n\n\n" +
//                "curl --location --request POST 'ops-bk.sandbox.fynarfin.io/oauth/token?username=mifos&password=password&grant_type=password' --header 'Platform-TenantId: gorilla' --header 'Authorization: Basic Y2xpZW50Og==' --header 'Content-Type: text/plain' --data-raw '{}'\n\n"
//)
@Configuration
//@EnableWebSecurity
public class WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/v3/api-docs/**",
                                        "/swagger-resources/**",
                                        "/swagger-ui/**",
                                        "/swagger-config/**",
                                        "/api/v1/errorcode/**",
                                        "/oauth/token").permitAll()
                                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                                .anyRequest().permitAll()
                ).build();
    }

    @Bean
    public OperationCustomizer customGlobalHeaders() {

        return (Operation operation, HandlerMethod handlerMethod) -> {

            Parameter missingParam1 = new HeaderParameter()
                    .in(ParameterIn.HEADER.toString())
                    .name("Platform-TenantId")
                    .schema(new StringSchema())
                    .description("Tenant Id")
                    .required(true);
            operation.addParametersItem(missingParam1);
            return operation;
        };
    }
}
