package by.ibn.alisamqttbridge.service;

import java.math.BigDecimal;
import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import by.ibn.alisamqttbridge.resources.CallbackRequest;
import by.ibn.alisamqttbridge.resources.CallbackResponse;
import by.ibn.alisamqttbridge.resources.Payload;

@Service
public class OutgoingDiscoveryService {

	private Logger log = LoggerFactory.getLogger(OutgoingDiscoveryService.class);

	@Autowired
	private RestTemplate restTemplate;

	@Value("${devices.payload.userId:66211404}")
	private String userId;
	
	@Value("${yandex.token:}")
	private String token;

	@Value("${yandex.skillId:}")
	private String skillId;

	@Value("${yandex.stateUrl:https://dialogs.yandex.net/api/v1/skills/${yandex.skillId:}/callback/discovery}")
	private String discoveryUrl;

	public void reportDiscovery() {

		if (StringUtils.isNoneBlank(token, skillId)) {

			log.trace("Reporting discovery to the {} ...", discoveryUrl);

			CallbackRequest request = new CallbackRequest();
			request.timestamp = BigDecimal.valueOf(System.currentTimeMillis() / 100, 1);
			request.payload = new Payload();
			request.payload.userId = userId;

			URI uri = UriComponentsBuilder.fromHttpUrl(discoveryUrl).build().toUri();

			HttpHeaders headers = new HttpHeaders();
			headers.set(HttpHeaders.AUTHORIZATION, "OAuth " + token);
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<CallbackRequest> entity = new HttpEntity<>(request, headers);

			try {
				ResponseEntity<CallbackResponse> response = restTemplate.exchange(uri, HttpMethod.POST, entity, CallbackResponse.class);
				
				if (response.getStatusCode().is2xxSuccessful()) {
					log.trace("Reporting discovery successful.");
				} else {
					CallbackResponse responseBody = response.getBody();
					if (responseBody == null) {
						log.trace("Reporting discovery unsucessful. HTTP Status Code: {}", response.getStatusCode());
					} else {
						log.trace("Reporting discovery unsuccessful. Error code: {}, Error message: {}", responseBody.errorCode, responseBody.errorMessage);
					}
				}
				
			} catch (Exception e) {
				log.error("Error reporting discovery", e);
			}

		}

	}

}
