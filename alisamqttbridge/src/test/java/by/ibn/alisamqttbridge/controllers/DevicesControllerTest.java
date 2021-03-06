package by.ibn.alisamqttbridge.controllers;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import by.ibn.alisamqttbridge.MQTTConfiguration;
import by.ibn.alisamqttbridge.resources.Response;
import by.ibn.alisamqttbridge.service.IncomingDevicesService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DevicesControllerTest {

	// just to disable
	@MockBean MQTTConfiguration mqtt;
	@MockBean IMqttClient mqttClient;
	@MockBean MqttConnectOptions mqttConnectOptions;

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private IncomingDevicesService service;
	
	@Test
	void testGet() throws Exception {

		String expectedRequestId = "_requestid";
		
		Response expectedResponse = new Response();
		expectedResponse.requestId = expectedRequestId;
		Mockito.when(service.getDevices(Mockito.eq(expectedRequestId))).thenReturn(expectedResponse);
		
		mvc
		.perform(MockMvcRequestBuilders.get("/user/devices")
				.header("X-Request-Id", expectedRequestId)
				.with( SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("SCOPE_api")) )
				.accept(MediaType.APPLICATION_JSON) 
		)
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.content().json("{\"request_id\":\"_requestid\"}}"));

		Mockito.verify(service, Mockito.times(1)).getDevices(Mockito.any());

	}

	@Test
	void testGetUnscoped() throws Exception {
		
		mvc
		.perform(MockMvcRequestBuilders.get("/user/devices")
				.header("X-Request-Id", "_requestId")
				.with( SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("SCOPE_read")) )
				.accept(MediaType.APPLICATION_JSON) 
		)
		.andExpect(MockMvcResultMatchers.status().isForbidden());
		
		Mockito.verify(service, Mockito.times(0)).getDevices(Mockito.any());
		
	}
	
	@Test
	void testGetForbidden() throws Exception {
		
		mvc
		.perform(MockMvcRequestBuilders.get("/user/devices")
				.header("X-Request-Id", "_requestId")
				.with( SecurityMockMvcRequestPostProcessors.jwt() )
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(MockMvcResultMatchers.status().isForbidden());
		
		Mockito.verify(service, Mockito.times(0)).getDevices(Mockito.any());
		
	}
	
	@Test
	void testgetNoAuth() throws Exception {
		
		mvc
		.perform(MockMvcRequestBuilders.get("/user/devices")
				.header("X-Request-Id", "_requestId")
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(MockMvcResultMatchers.status().isUnauthorized());
		
		Mockito.verify(service, Mockito.times(0)).getDevices(Mockito.any());
		
	}

	@Test
	void testInternalServerError() throws Exception {

		Mockito.when(service.getDevices(Mockito.any())).thenThrow(new RuntimeException("boa"));
		
		mvc
		.perform(MockMvcRequestBuilders.get("/user/devices")
				.header("X-Request-Id", "_requestId")
				.with( SecurityMockMvcRequestPostProcessors.jwt().authorities(new SimpleGrantedAuthority("SCOPE_api")) )
				.accept(MediaType.APPLICATION_JSON)
		)
		.andExpect(MockMvcResultMatchers.status().isInternalServerError());

		Mockito.verify(service, Mockito.times(1)).getDevices(Mockito.any());

	}

	
}
