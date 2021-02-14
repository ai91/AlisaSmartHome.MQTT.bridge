package by.ibn.alisamqttbridge.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueMapTemplate extends ValueMap {
	
	public String template;

	@Override
	public boolean isApplicable(Object value) {
		return true;
	}

	@Override
	public String map(Object value) {
		
		Map<String, String> params = new HashMap<>();
		if (value instanceof Map) {
			Map<?, ?> valueMap = (Map<?, ?>) value;
			for(Object key: valueMap.keySet()) {
				try {
					params.put(key.toString(), valueMap.get(key).toString());
				} catch (Exception ex) {}
			}
		} else {
			params.put("value", value.toString());
		}
		
		StringSubstitutor sub = new StringSubstitutor(params);
		return sub.replace(template);
	}

	@Override
	public boolean isValidConfig() {
		return StringUtils.isNotBlank(template);
	}

}
