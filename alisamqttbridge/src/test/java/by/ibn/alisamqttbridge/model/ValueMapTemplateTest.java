package by.ibn.alisamqttbridge.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import by.ibn.alisamqttbridge.TestUtil;

class ValueMapTemplateTest {

	@Test
	void test() throws JsonMappingException, JsonProcessingException {
		
		String jsonString = TestUtil.readResource("/valuemapstest/template.json");
		
		ValueMapTemplate valueMap = new ObjectMapper().readerFor(ValueMapTemplate.class).readValue(jsonString);
		
		assertTrue(valueMap.isApplicable(null));
		assertTrue(valueMap.isApplicable("1"));
		assertTrue(valueMap.isApplicable("on"));
		assertTrue(valueMap.isApplicable("cmd23"));
		
		assertEquals("(${h},${s},${v})", valueMap.map("1"));
		
		TreeMap<Object, Object> map = new TreeMap<Object, Object>();
		map.put("h", "1");
		map.put("s", Integer.valueOf(2));
		map.put("v", new BigDecimal(3));
		assertEquals("(1,2,3)", valueMap.map(map));
		
	}

}
