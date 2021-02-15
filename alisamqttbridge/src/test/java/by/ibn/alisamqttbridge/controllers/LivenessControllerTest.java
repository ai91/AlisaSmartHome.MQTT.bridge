package by.ibn.alisamqttbridge.controllers;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import by.ibn.alisamqttbridge.MQTTConfiguration;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class LivenessControllerTest {

	// just to disable
	@MockBean MQTTConfiguration mqtt;
	@MockBean IMqttClient mqttClient;
	@MockBean MqttConnectOptions mqttConnectOptions;

	@Autowired
	private MockMvc mvc;
	
	@Test
	void testHead() throws Exception {

		mvc
		.perform(MockMvcRequestBuilders.head("/"))
		.andExpect(MockMvcResultMatchers.status().isOk());

	}
	
}
