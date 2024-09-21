package by.ibn.alisamqttbridge.model;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueMapRegex extends ValueMap {

	public String search;
	public String replace;
	
	@Override
	public boolean isApplicable(Object value) {
		return value != null && Pattern.matches(search, value.toString());
	}

	@Override
	public String map(Object value) {
		return value.toString().replaceAll(search, replace);
	}

	@Override
	public boolean isValidConfig() {
		return StringUtils.isNoneBlank(search, replace);
	}

}
