package by.ibn.alisamqttbridge.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import by.ibn.alisamqttbridge.model.DevicesConfig;
import by.ibn.alisamqttbridge.model.ValueMap;
import by.ibn.alisamqttbridge.resources.Capability;
import by.ibn.alisamqttbridge.resources.Device;
import by.ibn.alisamqttbridge.resources.DeviceBridgingRule;
import by.ibn.alisamqttbridge.resources.MQTTState;
import by.ibn.alisamqttbridge.resources.Property;

@Component
public class DeviceRepository {
	
	private static final String DEVICES_CONFIG_NAME = "devices.json";

	private Logger log = LoggerFactory.getLogger(DeviceRepository.class);
	
	@Value("${devicespathprefix:file:}")
	private String configPathPrefix;
	
	@Value("${devicespath:./config}") 
	private String configPath;
	
	private List<Device> devices;
	
	public List<Device> getDeviceResources() {
		
		return getDevices().stream().map( d -> (Device)d).collect(Collectors.toList());
		
	}
	
	public Optional<Device> getDeviceById(String id)
	{
		return getDevices().stream()
				.filter( device -> StringUtils.equals(id, device.id) )
				.findFirst();
	}
	
	public List<Device> getDevices() {
		
		if (devices == null) {
			devices = loadDevices();
		}
		
		return devices;
	}

	private List<Device> loadDevices() {
		
		String blessedConfigPath = StringUtils.removeEnd(StringUtils.removeEnd(configPath, "/"), "\\");
		
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		
		String configResourceLocation = configPathPrefix + blessedConfigPath + "/" + DEVICES_CONFIG_NAME;

		log.info("Loading devices info from {}", configResourceLocation);
		
		Resource configResource = resourceLoader.getResource(configResourceLocation);
		
		try (Reader reader = new InputStreamReader(configResource.getInputStream(), StandardCharsets.UTF_8)) {
		    String payloadStr = FileCopyUtils.copyToString(reader);
		    DevicesConfig config = new ObjectMapper().readerFor(DevicesConfig.class).readValue(payloadStr);
		    
		    // now validate and keep only valid devices
		    if (config == null || config.devices == null) {
		    	log.error("Empty devices config. Configuration location: {}", configResourceLocation);
		    	throw new RuntimeException("Empty devices config");
		    }
		    
		    log.trace("Loaded {} devices. Validating and cleaning up...", config.devices.size());
		    
		    List<Device> devices = new ArrayList<Device>();
		    for (Device device: config.devices) {
		    	boolean deviceMisconfigured = false;
		    	// skip empty id's - they probably temporary disabled
		    	if (StringUtils.isNotBlank(device.id)) {
		    		log.trace("  device {}. Analysing...", device.id);
		    		
		    		// scan capabilities and initialize device states 
		    		if (device.capabilities != null) {
		    			
		    			log.trace("  found {} capabilities. Validating and cleaning up...", device.capabilities.size());
		    			
		    			for (Capability capability: device.capabilities) {

				    		log.trace("    capability {}. Analysing...", capability.type);
		    				
		    				if (capability.rules != null) {

				    			log.trace("    found {} rules definition(s). Validating...", capability.rules.size());

				    			String instance = null;
				        		for (DeviceBridgingRule rule: capability.rules) {
				        			if (rule.alisa == null || StringUtils.isBlank(rule.alisa.instance)) {
				        				log.warn("Device {} has misconfigured rule on capability {}: no alisa.instance. Device will be skipped.", device.id, capability.type);
				        				deviceMisconfigured = true;
				        			}
				        			if (instance != null && !StringUtils.equals(instance, rule.alisa.instance)) {
				        				log.warn("Device {} has misconfigured rule on capability {}: all capability's alisa.instance must be same. Device will be skipped.", device.id, capability.type);
				        				deviceMisconfigured = true;
				        			}
				        			instance = rule.alisa.instance;
				        			if (rule.mqtt == null || StringUtils.isAllBlank(rule.mqtt.commands, rule.mqtt.state)) {
				        				log.warn("Device {} has misconfigured rule on capability {}: no mqtt.commands and mqtt.state are defined. Device will be skipped.", device.id, capability.type);
				        				deviceMisconfigured = true;
				        			}
				        			if (rule.valueMapsToAlisa != null) {
				        				for (ValueMap valueMap: rule.valueMapsToAlisa) {
				        					if (!valueMap.isValidConfig()) {
				        						log.error("Device {} has misconfigured valueMapsToAlisa on capability {}. See {} definition.", device.id, capability.type, valueMap.getClass().getName());
				        						throw new RuntimeException("Misconfigured valueMapsToAlisa on " + device.id);
				        					}
				        				}
				        			}
				        			if (rule.valueMapsToMqtt != null) {
				        				for (ValueMap valueMap: rule.valueMapsToMqtt) {
				        					if (!valueMap.isValidConfig()) {
				        						log.error("Device {} has misconfigured valueMapsToMqtt on capability {}. See {} definition.", device.id, capability.type, valueMap.getClass().getName());
				        						throw new RuntimeException("Misconfigured valueMapsToMqtt on " + device.id);
				        					}
				        				}
				        			}
				        			if (!deviceMisconfigured && StringUtils.isNotBlank(rule.mqtt.state)) {
				        				rule.mqttState = new MQTTState();
				        			}
				        			
				        			log.trace("      rule added.");
				        		}
			    			}
		    			}
		    		}
		    		
		    		// scan properties and initialize device states 
		    		if (device.properties != null) {
		    			
		    			log.trace("  found {} properties. Validating and cleaning up...", device.properties.size());
		    			
		    			for (Property property: device.properties) {
		    				
		    				log.trace("    property {}. Analysing...", property.type);
		    				
		    				if (property.rules != null) {
		    					
		    					log.trace("    found {} rules definition(s). Validating...", property.rules.size());

		    					String instance = null;
		    					for (DeviceBridgingRule rule: property.rules) {
		    						if (rule.alisa == null || StringUtils.isBlank(rule.alisa.instance)) {
		    							log.warn("Device {} has misconfigured rule on property {}: no alisa.instance. Device will be skipped.", device.id, property.type);
		    							deviceMisconfigured = true;
		    						}
				        			if (instance != null && !StringUtils.equals(instance, rule.alisa.instance)) {
				        				log.warn("Device {} has misconfigured rule on property {}: all capability's alisa.instance must be same. Device will be skipped.", device.id, property.type);
				        				deviceMisconfigured = true;
				        			}
				        			instance = rule.alisa.instance;
		    						if (rule.mqtt == null || StringUtils.isBlank(rule.mqtt.state)) {
		    							log.warn("Device {} has misconfigured rule on property {}: no mqtt.state is defined. Device will be skipped.", device.id, property.type);
		    							deviceMisconfigured = true;
		    						}
		    						if (rule.mqtt != null && StringUtils.isNotBlank(rule.mqtt.commands)) {
		    							log.warn("Device {} has misconfigured rule on property {}: mqtt.commands is defined, but properties are readonly by definition.", device.id, property.type);
		    						}
		    						if (rule.valueMapsToAlisa != null) {
		    							for (ValueMap valueMap: rule.valueMapsToAlisa) {
		    								if (!valueMap.isValidConfig()) {
		    									log.error("Device {} has misconfigured valueMapsToAlisa on property {}. See {} definition.", device.id, property.type, valueMap.getClass().getName());
		    									throw new RuntimeException("Misconfigured valueMapsToAlisa on " + device.id);
		    								}
		    							}
		    						}
		    						if (rule.valueMapsToMqtt != null) {
		    							log.warn("Device {} has misconfigured rule on property {}: valueMapsToMqtt is defined, but properties are readonly by definition.", device.id, property.type);
		    						}
		    						
		    						log.trace("      rule added.");
		    					}
		    				}
		    			}
		    		}
		    		
		    		if (!deviceMisconfigured) {
		    			devices.add(device);
		    			log.trace("  device added.");
		    		}
		    	} else {
		    		log.trace("Skipping device without id");
		    	}
		    }
		    
		    return devices;
		    
		} catch (IOException e) {
			
			log.error("");
			log.error("Can't load devices config. Please make sure you configured application properly.");
			log.error("By default application searches for configuration in the ./config/devices.json file.");
			log.error("If used ai91/alexamqttbridge docker, then it's equivalent to /workspace/config/devices.json file.");
			log.error("Nevertheless it's possible to configure another path in different ways.");
			log.error("Few examples for /app/config/devices.json:");
			log.error("  - Environment variable DEVICESPATH. Example: export DEVICESPATH=/app/config");
			log.error("  - Commandline argument -DDEVICESPATH. Example: java -jar alisamqttbridge.jar  -DDEVICESPATH=/app/config");
			log.error("  - Files ./application.properties or ./config/application.properties with property name 'devicespath'. Example: echo devicespath=/app/config > ./application.properties && java -jar alisamqttbridge.jar");
			log.error("");
			
		    throw new UncheckedIOException(e);
		}
	}
	
}
