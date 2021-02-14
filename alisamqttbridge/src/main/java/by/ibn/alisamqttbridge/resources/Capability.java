package by.ibn.alisamqttbridge.resources;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Capability {
	
	public String type;
	
	public Boolean retrievable;
	
	public Boolean reportable;
	
	public Parameters parameters;
	
	public State state;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	public List<DeviceBridgingRule> rules;

}
