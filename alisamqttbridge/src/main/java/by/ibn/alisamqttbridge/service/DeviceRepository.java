package by.ibn.alisamqttbridge.service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
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

import by.ibn.alisamqttbridge.model.Device;
import by.ibn.alisamqttbridge.model.DeviceBridgingRule;
import by.ibn.alisamqttbridge.model.DeviceState;
import by.ibn.alisamqttbridge.model.DevicesConfig;
import by.ibn.alisamqttbridge.model.ValueMap;
import by.ibn.alisamqttbridge.resources.DeviceResource;

@Component
public class DeviceRepository {
	
	private static final String DEVICES_CONFIG_NAME = "devices.json";

	private Logger log = LoggerFactory.getLogger(DeviceRepository.class);
	
	@Value("${devicespathprefix:file:}")
	private String configPathPrefix;
	
	@Value("${devicespath:./config}") 
	private String configPath;
	
	private List<Device> devices;
	
	public List<DeviceResource> getDeviceResources() {
		
		return getDevices().stream().map( d -> (DeviceResource)d).collect(Collectors.toList());
		
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
		
		try (Reader reader = new InputStreamReader(configResource.getInputStream())) {
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
		    	// skip empty id's - they probably temporary disabled
		    	if (StringUtils.isNotBlank(device.id)) {
		    		log.trace("  device {}. Analysing...", device.id);
		    		// skip invalid rules and initialize device states 
		    		List<DeviceBridgingRule> rules = new ArrayList<>();
		    		List<DeviceState> states = new ArrayList<>();
		    		if (device.rules != null) {
		    			
		    			log.trace("  found {} rule definitions. Validating and cleaning up...", device.rules.size());
		    			
		        		for (DeviceBridgingRule rule: device.rules) {
		        			if (rule.alisa == null || StringUtils.isBlank(rule.alisa.instance)) {
		        				log.warn("Device {} has misconfigured rule: no alisa.instance. Skipping rule definition.", device.id);
		        				continue;
		        			}
		        			if (StringUtils.isAllBlank(rule.alisa.capability, rule.alisa.property)) {
		        				log.warn("Device {} has misconfigured rule: boath alisa.capability and alisa.property are empty. Skipping rule definition.", device.id);
		        				continue;
		        			}
		        			if (rule.mqtt == null || StringUtils.isAllBlank(rule.mqtt.commands, rule.mqtt.state)) {
		        				log.warn("Device {} has misconfigured rule: no mqtt.commands and mqtt.state are defined. Skipping rule definition.", device.id);
		        				continue;
		        			}
		        			if ((rule.valueMapsToAlisa == null || rule.valueMapsToAlisa.isEmpty()) && 
		        					(rule.valueMapsToMqtt == null || rule.valueMapsToMqtt.isEmpty())) {
		        				log.warn("Device {} has misconfigured rule: no valueMapsToAlisa and valueMapsToMqtt are defined. Skipping rule definition.", device.id);
		        				continue;
		        			}
		        			if (rule.valueMapsToAlisa != null) {
		        				for (ValueMap valueMap: rule.valueMapsToAlisa) {
		        					if (!valueMap.isValidConfig()) {
		        						log.error("Device {} has misconfigured valueMapsToAlisa", device.id);
		        						throw new RuntimeException("Misconfigured valueMapsToAlisa on " + device.id);
		        					}
		        				}
		        			}
		        			if (rule.valueMapsToMqtt != null) {
		        				for (ValueMap valueMap: rule.valueMapsToMqtt) {
		        					if (!valueMap.isValidConfig()) {
		        						log.error("Device {} has misconfigured valueMapsToMqtt", device.id);
		        						throw new RuntimeException("Misconfigured valueMapsToMqtt on " + device.id);
		        					}
		        				}
		        			}
		        			rules.add(rule);
		        			DeviceState state = new DeviceState();
		        			if (StringUtils.isNotBlank(rule.mqtt.state)) {
			        			state.capability = rule.alisa.capability;
			        			state.property = rule.alisa.property;
			        			state.instance = rule.alisa.instance;
			        			state.state = "";
			        			states.add(state);
		        			}
		        			
		        			log.trace("    rule added.");
		        		}
		    		}
		    		if (rules.isEmpty()) {
		    			log.warn("Device {} has no rules defined. Skipping device.", device.id  );
		    			continue;
		    		}
		    		device.rules = rules;
		    		device.states = states;
		    		devices.add(device);
        			log.trace("  device added.");
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
			log.error("  - Commandline argument -DDEVICESPATH. Example: java -jar amba.jar  -DDEVICESPATH=/app/config");
			log.error("  - Files ./application.properties or ./config/application.properties with property name 'devicespath'. Example: echo devicespath=/app/config > ./application.properties && java -jar amba.jar");
			log.error("");
			
		    throw new UncheckedIOException(e);
		}
	}
	
}
