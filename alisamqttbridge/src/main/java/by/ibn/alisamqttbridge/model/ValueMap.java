package by.ibn.alisamqttbridge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "type"
)
@JsonSubTypes({
	@Type(value = ValueMapValue.class, name = "value"),
	@Type(value = ValueMapLinearRange.class, name = "linearRange"),
	@Type(value = ValueMapRegex.class, name = "regex"),
	@Type(value = ValueMapStatic.class, name = "static"),
	@Type(value = ValueMapFormula.class, name = "formula"),
	@Type(value = ValueMapTemplate.class, name = "template")
})
public abstract class ValueMap {
	
	public abstract boolean isApplicable(Object value);
	
	public abstract String map(Object value);
	
	public abstract boolean isValidConfig();

}
