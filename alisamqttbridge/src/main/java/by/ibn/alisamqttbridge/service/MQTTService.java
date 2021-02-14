package by.ibn.alisamqttbridge.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	private Map<String, List<MQTTState>> subscriptions = new HashMap<>();
	
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

		
		String topic = rule.mqtt.state;
		
		if (StringUtils.isNotBlank(topic)) {
			
			
			rule.mqttState = new MQTTState();
			List<MQTTState> stateListeners = subscriptions.get(topic);
			if (stateListeners == null) {
				
				log.trace("Subscribing on topic {}", topic);
				
				stateListeners = new ArrayList<>();
				subscriptions.put(topic, stateListeners);
				
				stateListeners.add(rule.mqttState);
				
				try {
					
					mqttClient.subscribeWithResponse(topic, (tpic, msg) -> {
						String value = new String(msg.getPayload());
						log.trace("Received message on topic {}: {}", tpic, value );
						
						for(MQTTState mqttState: subscriptions.get(topic)) {
							mqttState.state = value;
						}
						
					});
					
				} catch (MqttException e) {
					
					log.error("Error while subscribing on topic {} for instance {}", topic, rule.alisa.instance);
					
				}
				
			} else if (stateListeners.size() > 0) {
				
				log.trace("Already subscribed on topic {}. Reusing existing subscription.", topic);
				
				// clone previously received state
				rule.mqttState.state = stateListeners.get(0).state;
				stateListeners.add(rule.mqttState);
			}
			
		}
	}	
}
