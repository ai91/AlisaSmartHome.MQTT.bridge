package by.ibn.alisamqttbridge.model;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueMapValue extends ValueMap {

	public String from;
	
	public String to;

	@Override
	public boolean isApplicable(Object value) {
		return value != null && StringUtils.equalsIgnoreCase(value.toString(), from);
	}

	@Override
	public String map(Object value) {
		return to;
	}

	@Override
	public boolean isValidConfig() {
		return StringUtils.isNoneBlank(from, to);
	}

}
