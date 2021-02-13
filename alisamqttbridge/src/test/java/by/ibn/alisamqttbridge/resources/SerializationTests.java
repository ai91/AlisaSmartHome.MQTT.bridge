package by.ibn.alisamqttbridge.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import by.ibn.alisamqttbridge.TestUtil;

class SerializationTests {
	
	@Test
	void deviceInfo() throws JsonMappingException, JsonProcessingException {
		
		String expectedString = TestUtil.readResource("/serializationtest/devices_response.json");
		
		Response response = new ObjectMapper().readerFor(Response.class).readValue(expectedString);
		
		assertEquals("ff36a3cc-ec34-11e6-b1a0-64510650abcf", response.requestId);
		
		String json = new ObjectMapper().writerFor(Response.class).writeValueAsString(response);
		
		assertEquals(expectedString.replaceAll("\\s+", ""), json.replaceAll("\\s+", ""));

	}
	
}
