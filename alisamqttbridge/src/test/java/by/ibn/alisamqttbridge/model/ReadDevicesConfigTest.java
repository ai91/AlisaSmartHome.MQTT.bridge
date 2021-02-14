package by.ibn.alisamqttbridge.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import by.ibn.alisamqttbridge.TestUtil;

class ReadDevicesConfigTest {

	@Test
	void testConfig() throws JsonMappingException, JsonProcessingException {
		
		String jsonString = TestUtil.readResource("/devices.json");
		
		DevicesConfig devicesConfig = new ObjectMapper().readerFor(DevicesConfig.class).readValue(jsonString);
		
		assertNotNull(devicesConfig);
		assertNotNull(devicesConfig.devices);
		assertEquals(1, devicesConfig.devices.size());
		assertEquals("abc-123", devicesConfig.devices.get(0).id);
		assertNotNull(devicesConfig.devices.get(0).dynamicProperties.get("custom_data"));
		assertEquals("two", ((Map<String, String>)devicesConfig.devices.get(0).dynamicProperties.get("custom_data")).get("bar"));
		assertNotNull(devicesConfig.devices.get(0).capabilities.get(0).rules);
		assertEquals(1, devicesConfig.devices.get(0).capabilities.get(0).rules.size());
		assertNotNull(devicesConfig.devices.get(0).capabilities.get(0).rules.get(0).alisa);
		assertNotNull(devicesConfig.devices.get(0).capabilities.get(0).rules.get(0).mqtt);
		assertEquals("house/outlet/bedroom/statebrightness", devicesConfig.devices.get(0).capabilities.get(0).rules.get(0).mqtt.state);
		assertNotNull(devicesConfig.devices.get(0).capabilities.get(0).rules.get(0).valueMapsToAlisa);
		assertEquals(1, devicesConfig.devices.get(0).capabilities.get(0).rules.get(0).valueMapsToAlisa.size());
		assertEquals(Boolean.TRUE, ((ValueMapLinearRange)devicesConfig.devices.get(0).capabilities.get(0).rules.get(0).valueMapsToAlisa.get(0)).rounded);
		assertNotNull(devicesConfig.devices.get(0).capabilities.get(0).rules.get(0).valueMapsToMqtt);
		assertEquals(1, devicesConfig.devices.get(0).capabilities.get(0).rules.get(0).valueMapsToMqtt.size());
	}

}
