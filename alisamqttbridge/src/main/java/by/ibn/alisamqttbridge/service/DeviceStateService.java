package by.ibn.alisamqttbridge.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import by.ibn.alisamqttbridge.model.ValueMap;
import by.ibn.alisamqttbridge.resources.Capability;
import by.ibn.alisamqttbridge.resources.Device;
import by.ibn.alisamqttbridge.resources.DeviceBridgingRule;
import by.ibn.alisamqttbridge.resources.Property;
import by.ibn.alisamqttbridge.resources.State;

@Service
public class DeviceStateService {
	
	private Logger log = LoggerFactory.getLogger(DeviceStateService.class);
	
	@Autowired
	private DeviceRepository deviceRepository;

	public Device getDeviceState(String deviceId) {
		
		log.trace("  getting state on device: {}", deviceId);
		
		Device deviceStateReport = new Device();
		deviceStateReport.id = deviceId;
		
		Optional<Device> deviceOpt = findDevice(deviceId);
		if (deviceOpt.isPresent()) {
			Device device = deviceOpt.get();

			deviceStateReport.capabilities = new ArrayList<>();
			deviceStateReport.properties = new ArrayList<>();
			
			for (Capability capability: device.capabilities) {
				if (capability.rules != null) {
					
					for (DeviceBridgingRule rule: capability.rules) {
						Capability capabilityStateReport = new Capability();
						capabilityStateReport.type = capability.type;
						capabilityStateReport.state = new State();
						
						Object alisaValue = null;
						
						capabilityStateReport.state.instance = rule.alisa.instance;
						alisaValue = getAlisaValue(alisaValue, rule);
						
						if (alisaValue != null) {
							capabilityStateReport.state.value = alisaValue;
							deviceStateReport.capabilities.add(capabilityStateReport);
							
							log.trace("    sending report on capability: {}, sate: {} -> {}", rule.alisa.instance, rule.mqttState.state, alisaValue);
						} else {
							log.trace("    no report on capability: {}, mqtt sate: {}", rule.alisa.instance, rule.mqttState == null ? "[none]" : rule.mqttState.state);
						}
					}
					
				}
			}
			
			for (Property property: device.properties) {
				if (property.rules != null) {
					
					for (DeviceBridgingRule rule: property.rules) {
						Property propertyStateReport = new Property();
						propertyStateReport.type = property.type;
						propertyStateReport.state = new State();
						
						Object alisaValue = null;
						
						propertyStateReport.state.instance = rule.alisa.instance;
						alisaValue = getAlisaValue(alisaValue, rule);
						
						if (alisaValue != null) {
							propertyStateReport.state.value = alisaValue;
							deviceStateReport.properties.add(propertyStateReport);
							
							log.trace("    sending report on property: {}, sate: {} -> {}", rule.alisa.instance, rule.mqttState.state, alisaValue);
						}
					}
				}
			}
			
		} else {
			deviceStateReport.errorCode = "DEVICE_NOT_FOUND";
			deviceStateReport.errorMessage = "Неизвестное устройство";
			log.warn("  device not found: {}", deviceId);
		}
		
		return deviceStateReport;
	}

	Optional<Device> findDevice(String deviceId) {
		return deviceRepository.getDeviceById(deviceId);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	Object getAlisaValue(Object alisaValue, DeviceBridgingRule rule) {
		
		if (rule.mqttState != null) {
		
			String mqttValue = rule.mqttState.state;
			String mappedValue = null;
			
			// try to match first mapper. If none matches - simply pass value as it is.
			for (ValueMap valueMap: rule.valueMapsToAlisa) {
				if (valueMap.isApplicable(mqttValue)) {
					mappedValue = valueMap.map(mqttValue);
					break;
				}
			}
			
			if (mappedValue != null) {
				if (StringUtils.isBlank(rule.alisa.subvalue)) {
					alisaValue = castValue(mappedValue);
				} else {
					if (!(alisaValue instanceof Map<?, ?>)) {
						alisaValue = new HashMap<String, Object>();
					}
					((Map)alisaValue).put(rule.alisa.subvalue, castValue(mappedValue));
				}
			}
			
		}
		
		return alisaValue;
	}

	Object castValue(String value) {
		
		if (value == null) {
			return null;
		}
		
		if (StringUtils.equalsIgnoreCase(value, "null")) {
			return Boolean.valueOf(value);
		}
		
		if (StringUtils.startsWith(value, "\"") && StringUtils.endsWith(value, "\"")) {
			return StringUtils.substring(value, 1, -1);
		}
		
		if (StringUtils.equalsAnyIgnoreCase(value, "true", "false")) {
			return Boolean.valueOf(value);
		}
		if (value.matches("-?[0-9]+")) {
			try {
				return Long.valueOf(value);
			} catch (NumberFormatException e) {}
		}
		if (value.matches("[0-9.-]+")) {
			try {
				return new BigDecimal(value);
			} catch (NumberFormatException e) {}
		}
		try {
			TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
			HashMap<String,Object> jsonMap = new ObjectMapper().readValue(value, typeRef);
			if (jsonMap != null) {
				return jsonMap;
			}
		} catch (Exception e) {}
		return value;
	}
	

}
