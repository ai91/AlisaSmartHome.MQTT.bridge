package by.ibn.alisamqttbridge.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

class DeviceStateServiceTest {
	
	@Test
	void castValue() {
		
		DeviceStateService testee = new DeviceStateService();
		
		assertEquals("", testee.castValue(null));
		assertEquals("test", testee.castValue("test"));
		assertEquals(Long.valueOf(1), testee.castValue("1"));
		assertEquals(new BigDecimal(1).setScale(1), testee.castValue("1.0"));
		assertEquals(Boolean.TRUE, testee.castValue("true"));
		
		HashMap<String, Object> expectedMap = new HashMap<String, Object>();
		expectedMap.put("h", Integer.valueOf(100));
		expectedMap.put("s", Double.valueOf(20.1d));
		expectedMap.put("v", "13");
		assertEquals(expectedMap, testee.castValue("{\"h\":100,\"s\":20.1,\"v\":\"13\"}"));
		
	}

}
