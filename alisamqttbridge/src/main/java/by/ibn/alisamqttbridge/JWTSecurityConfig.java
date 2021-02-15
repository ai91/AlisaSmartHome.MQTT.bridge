package by.ibn.alisamqttbridge;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder.JwkSetUriJwtDecoderBuilder;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class JWTSecurityConfig extends WebSecurityConfigurerAdapter {
	
	private Logger log = LoggerFactory.getLogger(JWTSecurityConfig.class);
	
	@Value("${spring.security.oauth2.scope:api}")
	private String scope;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
          .authorizeRequests(authz -> authz
        	.antMatchers("/").permitAll()
            .antMatchers("/user/**").hasAuthority("SCOPE_" + scope)
            .anyRequest().authenticated())
          .oauth2ResourceServer(oauth2 -> oauth2.jwt());
	}
    
    @Bean
    public JwtDecoder customDecoder(OAuth2ResourceServerProperties properties) {
    	
    	if (StringUtils.isEmpty(properties.getJwt().getJwkSetUri())) {
    		
    		log.error("");
    		log.error("No OAuth2 JSON Web Key Sets URL for signature validation found. ");
    		log.error("JWKS must be configured via 'spring.security.oauth2.resourceserver.jwt.jwk-set-uri' spring property.");
    		log.error("Examples: ");
    		log.error("- put url value into config/application.properties");
    		log.error("- provide as a commandline argument -Dspring.security.oauth2.resourceserver.jwt.jwk-set-uri=https://example.com/auth/.well-known/openid-configuration/jwks");
    		log.error("- provide as an environment variable SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=https://example.com/auth/.well-known/openid-configuration/jwks");
    		log.error("");
    		log.error("Note: also don't forget to configure JWT scope via 'spring.security.oauth2.scope' property.");
    		log.error("");
    		
    	}

    	// add 'at+jwt' support
    	DefaultJOSEObjectTypeVerifier<SecurityContext> jwsTypeVerifier = new DefaultJOSEObjectTypeVerifier<>(JOSEObjectType.JWT, new JOSEObjectType("at+jwt"), null);
        JwkSetUriJwtDecoderBuilder jwtDecoderBuilder = NimbusJwtDecoder.withJwkSetUri(properties.getJwt().getJwkSetUri());
        jwtDecoderBuilder.jwtProcessorCustomizer( config -> config.setJWSTypeVerifier(jwsTypeVerifier));
		return jwtDecoderBuilder.build();
		
    }    
}