package by.ibn.alisamqttbridge.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class State {
	
	@JsonProperty("instance")
	public String instance;
	
	@JsonProperty("value")
	public Object value;
	
	@JsonProperty("action_result")
	public ActionResult actionResult;

}
