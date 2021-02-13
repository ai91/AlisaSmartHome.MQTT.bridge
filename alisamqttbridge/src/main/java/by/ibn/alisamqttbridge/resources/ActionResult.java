package by.ibn.alisamqttbridge.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActionResult {

	@JsonProperty("status")
	public String status;
	
	@JsonProperty("error_code")
	public String errorCode;
	
	@JsonProperty("error_message")
	public String errorMessage;
	
}
