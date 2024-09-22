package by.ibn.alisamqttbridge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import by.ibn.alisamqttbridge.TestUtil;
import by.ibn.alisamqttbridge.model.DevicesConfig;
import by.ibn.alisamqttbridge.resources.Device;
import by.ibn.alisamqttbridge.resources.DeviceBridgingRule;
import by.ibn.alisamqttbridge.resources.MQTTState;

class DeviceStateServiceTest {
	
	@Test
	void castValue() {
		
		DeviceStateService testee = new DeviceStateService();
		
		assertEquals(null, testee.castValue(null));
		assertEquals("test", testee.castValue("test"));
		assertEquals(Long.valueOf(1), testee.castValue("1"));
		assertEquals("1", testee.castValue("\"1\""));
		assertEquals(new BigDecimal(1).setScale(1), testee.castValue("1.0"));
		assertEquals(Boolean.TRUE, testee.castValue("true"));
		
		HashMap<String, Object> expectedMap = new HashMap<String, Object>();
		expectedMap.put("h", Integer.valueOf(100));
		expectedMap.put("s", Double.valueOf(20.1d));
		expectedMap.put("v", "13");
		assertEquals(expectedMap, testee.castValue("{\"h\":100,\"s\":20.1,\"v\":\"13\"}"));
		
	}

	@Test
	void testConfig() throws JsonMappingException, JsonProcessingException {
		
		String jsonString = TestUtil.readResource("/valuemapstest/device.json");
		
		DevicesConfig devicesConfig = new ObjectMapper().readerFor(DevicesConfig.class).readValue(jsonString);

		
		DeviceStateService testee = new DeviceStateService() {
			@Override
			Optional<Device> findDevice(String deviceId) {
				return Optional.of(devicesConfig.devices.get(0));
			}
		};
		
		MQTTState state = new MQTTState();
		
		List<DeviceBridgingRule> rules = devicesConfig.devices.get(0).capabilities.get(0).rules;
		for(DeviceBridgingRule rule: rules)
		{
			rule.mqttState = state;
		}
		
		state.state = "val12";
		Device deviceState = testee.getDeviceState("abc-123");
		assertEquals(Long.valueOf(12), deviceState.capabilities.get(0).state.value);

		state.state = "str12";
		deviceState = testee.getDeviceState("abc-123");
		assertEquals("12", deviceState.capabilities.get(0).state.value);
		
		state.state = "poooh12";
		deviceState = testee.getDeviceState("abc-123");
		assertEquals(0, deviceState.capabilities.size());
	}
	
}
