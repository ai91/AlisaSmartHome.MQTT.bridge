package by.ibn.alisamqttbridge.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import by.ibn.alisamqttbridge.TestUtil;
import by.ibn.alisamqttbridge.model.ValueMapLinearRange;

class ValueMapLinearRangeTest {

	@Test
	void test() throws JsonMappingException, JsonProcessingException {
		
		String jsonString = TestUtil.readResource("/valuemapstest/linearRange.json");
		
		ValueMapLinearRange valueMap = new ObjectMapper().readerFor(ValueMapLinearRange.class).readValue(jsonString);
		
		assertEquals(Boolean.TRUE, valueMap.rounded);
		
		assertTrue(valueMap.isApplicable("1"));
		assertTrue(valueMap.isApplicable("1.0"));
		assertTrue(valueMap.isApplicable("-1.0"));
		assertTrue(valueMap.isApplicable("13"));
		assertTrue(valueMap.isApplicable("0"));
		assertTrue(valueMap.isApplicable("100"));
		assertFalse(valueMap.isApplicable("on"));
		assertFalse(valueMap.isApplicable("TurnOn"));
		
		
		assertEquals("0", valueMap.map("14"));
		assertEquals("100", valueMap.map("0"));
		assertEquals("0", valueMap.map("50"));
		assertEquals("100", valueMap.map("-10"));
		assertEquals("50", valueMap.map("7"));
		assertEquals("75", valueMap.map("3.5"));
		
	}
	
	@Test
	void testFloat() {
		ValueMapLinearRange valueMap = new ValueMapLinearRange();
		valueMap.fromMin = 10f;
		valueMap.fromMax = 110f;
		valueMap.toMin = -10f;
		valueMap.toMax = 190f;
		valueMap.rounded = Boolean.FALSE;
		
		assertEquals("-10.0", valueMap.map("10"));
		assertEquals("190.0", valueMap.map("110"));
		assertEquals("10.0", valueMap.map("20"));
		assertEquals("90.0", valueMap.map("60"));
	}

	@Test
	void testNormalRanges() {
		ValueMapLinearRange valueMap = new ValueMapLinearRange();
		valueMap.fromMin = 10f;
		valueMap.fromMax = 110f;
		valueMap.toMin = -10f;
		valueMap.toMax = 190f;
		
		assertEquals("-10", valueMap.map("10"));
		assertEquals("190", valueMap.map("110"));
		assertEquals("10", valueMap.map("20"));
		assertEquals("90", valueMap.map("60"));
	}
	
	@Test
	void testReversedFromRange() {
		ValueMapLinearRange valueMap = new ValueMapLinearRange();
		valueMap.fromMin = 110f;
		valueMap.fromMax = 10f;
		valueMap.toMin = -10f;
		valueMap.toMax = 190f;
		
		assertEquals("190", valueMap.map("10"));
		assertEquals("-10", valueMap.map("110"));
		assertEquals("170", valueMap.map("20"));
		assertEquals("90", valueMap.map("60"));
	}
	
	@Test
	void testReversedToRange() {
		ValueMapLinearRange valueMap = new ValueMapLinearRange();
		valueMap.fromMin = 10f;
		valueMap.fromMax = 110f;
		valueMap.toMin = 190f;
		valueMap.toMax = -10f;
		
		assertEquals("190", valueMap.map("10"));
		assertEquals("-10", valueMap.map("110"));
		assertEquals("170", valueMap.map("20"));
		assertEquals("90", valueMap.map("60"));
	}
	
	@Test
	void testReversedBothRanges() {
		ValueMapLinearRange valueMap = new ValueMapLinearRange();
		valueMap.fromMin = 110f;
		valueMap.fromMax = 10f;
		valueMap.toMin = 190f;
		valueMap.toMax = -10f;
		
		assertEquals("-10", valueMap.map("10"));
		assertEquals("190", valueMap.map("110"));
		assertEquals("10", valueMap.map("20"));
		assertEquals("90", valueMap.map("60"));
	}

}
