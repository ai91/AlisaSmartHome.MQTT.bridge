package by.ibn.alisamqttbridge.service;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

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
public class OutgoingStateService {

	private Logger log = LoggerFactory.getLogger(OutgoingStateService.class);

	@Autowired
	private DeviceStateService deviceStateService;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${devices.payload.userId:66211404}")
	private String userId;
	
	@Value("${yandex.token:}")
	private String token;

	@Value("${yandex.skillId:}")
	private String skillId;

	@Value("${yandex.stateUrl:https://dialogs.yandex.net/api/v1/skills/${yandex.skillId:}/callback/state}")
	private String stateUrl;

	public void reportState(List<String> deviceIds) {

		if (StringUtils.isNoneBlank(token, skillId) && deviceIds != null && !deviceIds.isEmpty()) {

			log.trace("Reporting state of {} to the {} ...", deviceIds, stateUrl);

			CallbackRequest request = new CallbackRequest();
			request.timestamp = Float.valueOf(System.currentTimeMillis() / 1000);
			request.payload = new Payload();
			request.payload.userId = userId;
			request.payload.devices = deviceIds
					.stream()
					.map(deviceId -> deviceStateService.getDeviceState(deviceId))
					.collect(Collectors.toList());

			URI uri = UriComponentsBuilder.fromHttpUrl(stateUrl).build().toUri();

			HttpHeaders headers = new HttpHeaders();
			headers.set(HttpHeaders.AUTHORIZATION, "OAuth " + token);
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<CallbackRequest> entity = new HttpEntity<>(request, headers);

			try {
				ResponseEntity<CallbackResponse> response = restTemplate.exchange(uri, HttpMethod.POST, entity, CallbackResponse.class);
				
				if (response.getStatusCode().is2xxSuccessful()) {
					log.trace("Reporting state of {} successful.", deviceIds);
				} else {
					CallbackResponse responseBody = response.getBody();
					if (responseBody == null) {
						log.trace("Reporting state of {} unsucessful. HTTP Status Code: {}", deviceIds, response.getStatusCode());
					} else {
						log.trace("Reporting state of {} unsuccessful. Error code: {}, Error message: {}", deviceIds, responseBody.errorCode, responseBody.errorMessage);
					}
				}
				
			} catch (Exception e) {
				log.error("Error reporting state", e);
			}

		}

	}

}
