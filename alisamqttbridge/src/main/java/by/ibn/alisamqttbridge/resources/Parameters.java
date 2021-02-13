package by.ibn.alisamqttbridge.resources;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Parameters {
	
	@JsonIgnore
	public Map<String, Object> dynamicProperties;
	
	@JsonAnyGetter
	public Map<String, Object> getDynamicProperties() {
		return dynamicProperties;
	}
	
    @JsonAnySetter
    public void addDynamicProperties (String propertyKey, Object value) {
        if (this.dynamicProperties == null) {
            this.dynamicProperties = new LinkedHashMap<>();
        }
        this.dynamicProperties.put(propertyKey, value);
    }
    
}
