package by.ibn.alisamqttbridge.model;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueMapFormula extends ValueMap {

	public String formula;
	public Boolean rounded = Boolean.TRUE;
	
	@Override
	public boolean isApplicable(Object value) {
		if (value instanceof Map) {
			return true;
		}
		try {
			Double.parseDouble(value.toString());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String map(Object value) {
		
		Expression e = new ExpressionBuilder(formula)
				.variable("value")
				.build();
		
		if (value instanceof Map) {
			Map<?, ?> valueMap = (Map<?, ?>) value;
			for(Object key: valueMap.keySet()) {
				try {
					e.setVariable(key.toString(), Double.parseDouble(valueMap.get(key).toString()));
				} catch (Exception ex) {}
			}
		} else {
			try {
				e.setVariable("value", Double.parseDouble(value.toString()));
			} catch (Exception ex) {}
		}
		
		float resVal = (float) e.evaluate();
				
		if (rounded.booleanValue()) {			
			return String.valueOf(Math.round(resVal));
		} else {
			return String.valueOf(resVal);
		}
	}

	@Override
	public boolean isValidConfig() {
		return StringUtils.isNotBlank(formula);
	}

}
