package by.ibn.alisamqttbridge.model;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueMapStatic extends ValueMap {

	public String value;
	
	@Override
	public boolean isApplicable(String value) {
		return true;
	}

	@Override
	public String map(String value) {
		return this.value;
	}

	@Override
	public boolean isValidConfig() {
		return StringUtils.isNotBlank(value);
	}

}
