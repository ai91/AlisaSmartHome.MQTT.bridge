package by.ibn.alisamqttbridge.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import by.ibn.alisamqttbridge.TestUtil;
import by.ibn.alisamqttbridge.model.ValueMapStatic;

class ValueMapStaticTest {

	@Test
	void test() throws JsonMappingException, JsonProcessingException {
		
		String jsonString = TestUtil.readResource("/valuemapstest/static.json");
		
		ValueMapStatic valueMap = new ObjectMapper().readerFor(ValueMapStatic.class).readValue(jsonString);
		
		assertTrue(valueMap.isApplicable("cmd23"));
		
		assertEquals("http://someUrlToACameraStream", valueMap.map("cmd23"));
		
	}

}
