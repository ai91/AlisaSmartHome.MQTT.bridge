package by.ibn.alisamqttbridge.model;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueMapRegex extends ValueMap {

	public String search;
	public String replace;
	
	@Override
	public boolean isApplicable(String value) {
		return Pattern.matches(search, value);
	}

	@Override
	public String map(String value) {
		return value.replaceAll(search, replace);
	}

	@Override
	public boolean isValidConfig() {
		return StringUtils.isNoneBlank(search, replace);
	}

}
