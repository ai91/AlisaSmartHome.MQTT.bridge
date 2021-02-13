package by.ibn.alisamqttbridge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueMapLinearRange extends ValueMap {

	public Float fromMin;
	public Float fromMax;
	public Float toMin;
	public Float toMax;
	
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
		float val = Float.parseFloat(value);
		float min = Math.min(fromMin.intValue(), fromMax.intValue());
		float max = Math.max(fromMin.intValue(), fromMax.intValue());
		val = Math.max(val, min);
		val = Math.min(val, max);
		
		float valNorm = (val - min) / (max - min);
		if (fromMin.intValue() > fromMax.intValue()) {
			valNorm = 1 - valNorm;
		}
		
		float resVal = valNorm * (toMax.floatValue() - toMin.floatValue()) + toMin.floatValue();

		if (rounded.booleanValue()) {			
			return String.valueOf(Math.round(resVal));
		} else {
			return String.valueOf(resVal);
		}
	}

	@Override
	public boolean isValidConfig() {
		return fromMin != null && fromMax != null && toMin != null && toMax != null;
	}

}
