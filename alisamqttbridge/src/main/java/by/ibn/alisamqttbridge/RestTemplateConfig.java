package by.ibn.alisamqttbridge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
	
	@Autowired
	SimpleClientHttpRequestFactory simpleClientHttpRequestFactory;

	@Bean
	public RestTemplate restTemplate() {
		
		return new RestTemplate(simpleClientHttpRequestFactory);
		
	}
	
}
