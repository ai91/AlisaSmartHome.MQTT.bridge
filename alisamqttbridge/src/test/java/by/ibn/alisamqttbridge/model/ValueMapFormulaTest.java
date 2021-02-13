package by.ibn.alisamqttbridge.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import by.ibn.alisamqttbridge.TestUtil;
import by.ibn.alisamqttbridge.model.ValueMapFormula;

class ValueMapFormulaTest {

	@Test
	void test() throws JsonMappingException, JsonProcessingException {
		
		String jsonString = TestUtil.readResource("/valuemapstest/formula.json");
		
		ValueMapFormula valueMap = new ObjectMapper().readerFor(ValueMapFormula.class).readValue(jsonString);
		
		assertFalse(valueMap.isApplicable("on"));
		assertFalse(valueMap.isApplicable("cmd23"));
		assertTrue(valueMap.isApplicable("1"));
		
		assertEquals("0", valueMap.map("32"));
		assertEquals("3", valueMap.map("37"));
		assertEquals("38", valueMap.map("100"));
		
	}

}
