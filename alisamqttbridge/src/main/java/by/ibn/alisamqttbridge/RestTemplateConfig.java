package by.ibn.alisamqttbridge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
	
	@Autowired
	@Lazy
	SimpleClientHttpRequestFactory simpleClientHttpRequestFactory;

	@Bean
	public RestTemplate restTemplate() {
		
		return new RestTemplate(simpleClientHttpRequestFactory);
		
	}
	
	@Bean
	@ConfigurationProperties(prefix = "httpclient")
	public SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
		
		return new SimpleClientHttpRequestFactory();
		
	}

}
