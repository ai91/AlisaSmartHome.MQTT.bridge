package by.ibn.alisamqttbridge.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallbackResponse {
	
	@JsonProperty("request_id")
	public String requestId;
	
	@JsonProperty("status")
	public String status;

	@JsonProperty("error_code")
	public String errorCode;
	
	@JsonProperty("error_message")
	public String errorMessage;
	
}
