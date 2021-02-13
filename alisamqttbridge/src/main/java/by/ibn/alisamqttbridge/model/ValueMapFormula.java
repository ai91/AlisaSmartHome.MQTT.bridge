package by.ibn.alisamqttbridge.model;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueMapFormula extends ValueMap {

	public String formula;
	public Boolean rounded = Boolean.TRUE;
	
	@Override
	public boolean isApplicable(String value) {
		try {
			Float.parseFloat(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String map(String value) {
		
		Expression e = new ExpressionBuilder(formula)
				.variable("value")
				.build()
				.setVariable("value", Float.parseFloat(value));
		
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
