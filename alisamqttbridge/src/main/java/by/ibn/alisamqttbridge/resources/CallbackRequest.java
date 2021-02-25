package by.ibn.alisamqttbridge.resources;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallbackRequest {
	
	@JsonProperty("ts")
	public BigDecimal timestamp;
	
	@JsonProperty("payload")
	public Payload payload;

}
