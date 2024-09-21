package by.ibn.alisamqttbridge.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import by.ibn.alisamqttbridge.TestUtil;

class ValueMapStaticTest {

	@Test
	void test() throws JsonMappingException, JsonProcessingException {
		
		String jsonString = TestUtil.readResource("/valuemapstest/static.json");
		
		ValueMapStatic valueMap = new ObjectMapper().readerFor(ValueMapStatic.class).readValue(jsonString);
		
		assertTrue(valueMap.isApplicable(null));
		assertTrue(valueMap.isApplicable("cmd23"));
		
		assertEquals("http://someUrlToACameraStream", valueMap.map("cmd23"));
		
	}

}
