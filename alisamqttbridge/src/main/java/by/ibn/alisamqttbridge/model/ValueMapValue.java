package by.ibn.alisamqttbridge.model;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueMapValue extends ValueMap {

	public String from;
	
	public String to;

	@Override
	public boolean isApplicable(String value) {
		return StringUtils.equalsIgnoreCase(value, from);
	}

	@Override
	public String map(String value) {
		return to;
	}

	@Override
	public boolean isValidConfig() {
		return StringUtils.isNoneBlank(from, to);
	}

}
