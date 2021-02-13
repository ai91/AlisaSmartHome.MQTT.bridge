package by.ibn.alisamqttbridge.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceInfo {
	
	@JsonProperty("manufacturer")
	public String manufacturer;

	@JsonProperty("model")
	public String model;
	
	@JsonProperty("hw_version")
	public String hwVersion;
	
	@JsonProperty("sw_version")
	public String swVersion;
	
}
