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
		if (value != null)
		{
			Pattern p = Pattern.compile(search, Pattern.DOTALL);
			return p.matcher(value.toString()).find();
		}
		return false;
	}

	@Override
	public String map(Object value) {
		Pattern p = Pattern.compile(search, Pattern.DOTALL);
		return p.matcher(value.toString()).replaceAll(replace);
	}

	@Override
	public boolean isValidConfig() {
		return StringUtils.isNoneBlank(search, replace);
	}

}
