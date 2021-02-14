package by.ibn.alisamqttbridge.resources;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Device {
	
	@JsonProperty("id")
	public String id;

	@JsonProperty("name")
	public String name;
	
	@JsonProperty("description")
	public String description;
	
	@JsonProperty("room")
	public String room;
	
	@JsonProperty("type")
	public String type;
	
	@JsonProperty("capabilities")
	public List<Capability> capabilities;
	
	@JsonProperty("properties")
	public List<Property> properties;
	
	@JsonProperty("device_info")
	public DeviceInfo deviceInfo;
	
	@JsonProperty("error_code")
	public String errorCode;
	
	@JsonProperty("error_message")
	public String errorMessage;
	
	@JsonProperty("action_result")
	public ActionResult actionResult;
	
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
