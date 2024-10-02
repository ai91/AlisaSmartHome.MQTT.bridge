package by.ibn.alisamqttbridge;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
public class RestClientHttpRequestFactoryConfig {
	
	@Bean
	@ConfigurationProperties(prefix = "httpclient")
	public SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
		
		return new SimpleClientHttpRequestFactory();
		
	}

}
