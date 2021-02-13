package by.ibn.alisamqttbridge.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import by.ibn.alisamqttbridge.TestUtil;
import by.ibn.alisamqttbridge.model.ValueMapValue;

class ValueMapValueTest {

	@Test
	void test() throws JsonMappingException, JsonProcessingException {
		
		String jsonString = TestUtil.readResource("/valuemapstest/value.json");
		
		ValueMapValue valueMap = new ObjectMapper().readerFor(ValueMapValue.class).readValue(jsonString);
		
		assertFalse(valueMap.isApplicable("1"));
		assertFalse(valueMap.isApplicable("on"));
		assertTrue(valueMap.isApplicable("TurnOn"));
		
		assertEquals("1", valueMap.map("TurnOn"));
		
	}

}
