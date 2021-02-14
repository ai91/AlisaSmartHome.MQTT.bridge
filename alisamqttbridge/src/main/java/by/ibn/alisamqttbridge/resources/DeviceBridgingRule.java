package by.ibn.alisamqttbridge.resources;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import by.ibn.alisamqttbridge.model.ValueMap;

public class DeviceBridgingRule {

	public DeviceBridgingConfigAlisa alisa;
	
	public DeviceBridgingConfigMqtt mqtt;
	
	public List<ValueMap> valueMapsToAlisa;
	
	public List<ValueMap> valueMapsToMqtt;

	// current state of the mqtt.state topic
	@JsonIgnore
	public MQTTState mqttState;
	
}
