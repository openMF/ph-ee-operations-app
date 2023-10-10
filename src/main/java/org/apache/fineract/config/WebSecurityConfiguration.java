package org.apache.fineract.config;
import org.apache.fineract.auth.*;
import org.apache.fineract.core.tenants.TenantsService;
import org.apache.fineract.organisation.role.Role;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.apache.fineract.organisation.user.AppUser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.HandlerMethod;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

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
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Autowired
    private RsaKeyProperties rsaKeys;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${client.id}")
    private String clientId;

    @Value("${client.secret}")
    private String secret;

    @Value("${frontend.callback-url}")
    private String callbackUrl;


    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class).oidc(Customizer.withDefaults());
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(
                                new LoginUrlAuthenticationEntryPoint("/login"))
                );
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain apiFilter(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/swagger-ui/**",
                                "/swagger-config/**",
                                "/api/v1/errorcode/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                )
                .securityMatcher("/api/**")
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/**").authenticated())
                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(jwt -> jwt.jwtAuthenticationConverter(new ClaimConverter())));
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .securityMatcher("/oauth2/**", "/login", "/logout", "/images/**", "/css/**", "/favicon.ico")
                .authorizeHttpRequests(auth -> auth.requestMatchers("/oauth2/**", "/login", "/logout", "/images/**", "/css/**", "/favicon.ico").permitAll())
                .formLogin(form -> form.loginPage("/login")
                        .authenticationDetailsSource(new TenantAuthenticationDetailsSource()))
                .logout(lo -> lo.logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()));
        if (callbackUrl.startsWith("https")) {
            http.logout(lo -> lo.addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(
                    ClearSiteDataHeaderWriter.Directive.CACHE,
                    ClearSiteDataHeaderWriter.Directive.COOKIES,
                    ClearSiteDataHeaderWriter.Directive.STORAGE))));
        } else {
            http.logout(lo -> lo.addLogoutHandler(new HeaderWriterLogoutHandler(new UnsecureClearSiteDataHeaderWriter(
                    UnsecureClearSiteDataHeaderWriter.Directive.CACHE,
                    UnsecureClearSiteDataHeaderWriter.Directive.COOKIES,
                    UnsecureClearSiteDataHeaderWriter.Directive.STORAGE))));
        }
        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(passwordEncoder.encode(secret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope(OidcScopes.OPENID)
                .redirectUri(callbackUrl)
                .tokenSettings(TokenSettings.builder().accessTokenTimeToLive(Duration.ofHours(1)).build())
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).requireProofKey(true).build())
                .build();

        return new InMemoryRegisteredClientRepository(oidcClient);
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            UsernamePasswordAuthenticationToken authentication = context.getPrincipal();
            TenantAuthenticationDetails details = (TenantAuthenticationDetails) authentication.getDetails();
            AppUser appUser = (AppUser) authentication.getPrincipal();
            List<String> roles = appUser.getRoles().stream().map(Role::getName).toList();
            List<String> scope = appUser.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            List<String> finalScope = scope.isEmpty() ? List.of("all") : scope;
            context.getClaims()
                    .claim("scope", finalScope)
                    .claim("role", roles)
                    .claim("tenant", details.getTenant());
        };
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = new RSAKey.Builder(rsaKeys.publicKey())
                .privateKey(rsaKeys.privateKey())
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
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

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
