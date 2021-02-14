package by.ibn.alisamqttbridge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import by.ibn.alisamqttbridge.MQTTConfiguration;
import by.ibn.alisamqttbridge.resources.Device;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {"devicespath=/devicerepositorytest", "devicespathprefix=classpath:"})
class DeviceRepositoryTest {

	// just to disable
	@MockBean MQTTConfiguration mqtt;
	@MockBean IMqttClient mqttClient;
	@MockBean MqttConnectOptions mqttConnectOptions;

	@Autowired
	private DeviceRepository testee;
	
	@Test
	void loadConfig() {
		
		List<Device> devices = testee.getDeviceResources();
		
		assertNotNull(devices);
		assertEquals(1, devices.size());
		assertEquals("id1", devices.get(0).id);
		assertEquals("лампочка", devices.get(0).name);
		
	}

	@Test
	void getDeviceById() {
		
		Optional<Device> device = testee.getDeviceById("id1");
		
		assertTrue(device.isPresent());
		assertEquals("id1", device.get().id);
		
	}
	
}
