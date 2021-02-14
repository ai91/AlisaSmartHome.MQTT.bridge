package by.ibn.alisamqttbridge.service;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.ibn.alisamqttbridge.resources.Capability;
import by.ibn.alisamqttbridge.resources.Device;
import by.ibn.alisamqttbridge.resources.DeviceBridgingRule;
import by.ibn.alisamqttbridge.resources.MQTTState;
import by.ibn.alisamqttbridge.resources.Property;

@Service
public class MQTTService {

	private Logger log = LoggerFactory.getLogger(MQTTService.class);
	
	@Autowired
	private IMqttClient mqttClient;
	
	public void sendMessage(String topic, String value) {

		MqttMessage mqttMessage = new MqttMessage();
		mqttMessage.setPayload(value.getBytes());
		mqttMessage.setQos(2);
		mqttMessage.setRetained(false);

		try {
			
			mqttClient.publish(topic, mqttMessage);
			
		} catch (MqttException e) {
			
			log.error("Error while publishing message to the MQTT", e);
			
		}
	}
	
	public void subscribeOnTopics(DeviceRepository deviceRepository) {
		
		for (Device device: deviceRepository.getDevices()) {
			
			if (device.capabilities != null) {
				
				for (Capability capability: device.capabilities) {
					
					if (capability.rules != null) {
						
						for (DeviceBridgingRule rule: capability.rules) {
							subscribe(rule);
						}
						
					}
					
				}
				
			}
			
			if (device.properties != null) {
				
				for (Property property: device.properties) {
					
					if (property.rules != null) {
						
						for (DeviceBridgingRule rule: property.rules) {
							subscribe(rule);
						}
						
					}
					
				}
				
			}
			
		}
		
	}
	
	private void subscribe(DeviceBridgingRule rule) {

		
		if (StringUtils.isNotBlank(rule.mqtt.state)) {
			
			log.trace("Subscribing on topic {}", rule.mqtt.state);
			
			rule.mqttState = new MQTTState();
			
			try {
				
				mqttClient.subscribeWithResponse(rule.mqtt.state, (tpic, msg) -> {
					String value = new String(msg.getPayload());
					log.trace("Received message on topic {}: {}", tpic, value );
					
					rule.mqttState.state = value;
					
				});
				
			} catch (MqttException e) {
				
				log.error("Error while subscribing on topic {} for instance {}", rule.mqtt.state, rule.alisa.instance);
				
			}
		}
	}	
}
