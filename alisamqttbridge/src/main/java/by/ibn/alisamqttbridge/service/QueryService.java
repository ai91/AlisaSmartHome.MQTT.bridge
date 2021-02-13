package by.ibn.alisamqttbridge.service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.ibn.alisamqttbridge.model.Device;
import by.ibn.alisamqttbridge.model.DeviceBridgingRule;
import by.ibn.alisamqttbridge.model.DeviceState;
import by.ibn.alisamqttbridge.model.ValueMap;
import by.ibn.alisamqttbridge.resources.Capability;
import by.ibn.alisamqttbridge.resources.DeviceResource;
import by.ibn.alisamqttbridge.resources.Payload;
import by.ibn.alisamqttbridge.resources.Property;
import by.ibn.alisamqttbridge.resources.Request;
import by.ibn.alisamqttbridge.resources.Response;
import by.ibn.alisamqttbridge.resources.State;

@Service
public class QueryService {
	
	@Autowired
	private DeviceRepository deviceRepository;

	public Response getStates(Request request) {
		
		Response response = new Response();
		response.requestId = UUID.randomUUID().toString();
		
		response.payload = new Payload();
		
		if (request.devices != null) {
			
			response.payload.devices = request.devices.stream()
					.map( device -> device.id )
					.map( deviceId -> getDeviceState(deviceId) )
					.collect(Collectors.toList());
			
		}
				
		return response;
	}

	private DeviceResource getDeviceState(String deviceId) {
		
		DeviceResource deviceStateReport = new DeviceResource();
		deviceStateReport.id = deviceId;
		
		Optional<Device> deviceOpt = deviceRepository.getDeviceById(deviceId);
		if (deviceOpt.isPresent()) {
			Device device = deviceOpt.get();

			deviceStateReport.capabilities = new ArrayList<>();
			deviceStateReport.properties = new ArrayList<>();
			
			for (DeviceState deviceState: device.states) {

				for (DeviceBridgingRule rule: device.rules) {
					
					if ((StringUtils.isNotBlank(rule.alisa.capability) && StringUtils.equals(deviceState.capability, rule.alisa.capability) ||
						(StringUtils.isNotBlank(rule.alisa.property) && StringUtils.equals(deviceState.property, rule.alisa.property) )) &&
							StringUtils.equals(deviceState.instance, rule.alisa.instance)) {

						String alisaValue = deviceState.state;
						
						// try to match first mapper. If none matches - simply pass value as it is.
						for (ValueMap valueMap: rule.valueMapsToAlisa) {
							if (valueMap.isApplicable(deviceState.state)) {
								alisaValue = valueMap.map(deviceState.state);
								break;
							}
						}
						
						if (StringUtils.isNotBlank(rule.alisa.capability)) {
							Capability capability = new Capability();
							capability.type = rule.alisa.capability;
							capability.state = new State();
							capability.state.instance = rule.alisa.instance;
							capability.state.value = alisaValue;
							deviceStateReport.capabilities.add(capability);
						}
						
						if (StringUtils.isNotBlank(rule.alisa.property)) {
							Property property = new Property();
							property.type = rule.alisa.capability;
							property.state = new State();
							property.state.instance = rule.alisa.instance;
							property.state.value = alisaValue;
							deviceStateReport.properties.add(property);
						}
					}
				}
			}
		} else {
			deviceStateReport.errorCode = "DEVICE_NOT_FOUND";
			deviceStateReport.errorMessage = "Неизвестное устройство";
		}
		
		return deviceStateReport;
	}
	

}
