package by.ibn.alisamqttbridge.service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.ibn.alisamqttbridge.model.Device;
import by.ibn.alisamqttbridge.model.DeviceBridgingRule;
import by.ibn.alisamqttbridge.model.ValueMap;
import by.ibn.alisamqttbridge.resources.ActionResult;
import by.ibn.alisamqttbridge.resources.Capability;
import by.ibn.alisamqttbridge.resources.DeviceResource;
import by.ibn.alisamqttbridge.resources.Payload;
import by.ibn.alisamqttbridge.resources.Request;
import by.ibn.alisamqttbridge.resources.Response;
import by.ibn.alisamqttbridge.resources.State;

@Service
public class ActionService {

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private MQTTService mqttService;
	
	public Response doAction(Request request) {
		
		Response response = new Response();
		response.requestId = UUID.randomUUID().toString();
		
		response.payload = new Payload();
		
		if (request.devices != null) {
			
			response.payload.devices = request.devices.stream()
					.map( device -> doAction(device) )
					.collect(Collectors.toList());
			
		}
				
		return response;
		
	}

	private DeviceResource doAction(DeviceResource deviceRequest) {
		
		DeviceResource deviceStateReport = new DeviceResource();
		deviceStateReport.id = deviceRequest.id;
		
		Optional<Device> deviceOpt = deviceRepository.getDeviceById(deviceRequest.id);
		if (deviceOpt.isPresent()) {

			boolean sentAtLeastOne = false;
			Device device = deviceOpt.get();
			for(Capability capabilityRequest: deviceRequest.capabilities) {
				
				for (DeviceBridgingRule rule: device.rules) {
				
					if (StringUtils.equals(capabilityRequest.type, rule.alisa.capability) &&
							capabilityRequest.state != null &&
							StringUtils.equals(capabilityRequest.state.instance, rule.alisa.instance) &&
							rule.mqtt != null && StringUtils.isNotBlank(rule.mqtt.commands)
							&& capabilityRequest.state.value != null) {
						
						String alisaValue = capabilityRequest.state.value.toString();
						String mqttValue = alisaValue;
					
						for (ValueMap valueMap: rule.valueMapsToMqtt) {
							if (valueMap.isApplicable(alisaValue)) {
								mqttValue = valueMap.map(alisaValue);
								mqttService.sendMessage(rule.mqtt.commands, mqttValue);
								
								Capability capabilityReport = new Capability();
								capabilityReport.type = capabilityRequest.type;
								capabilityReport.state = new State();
								capabilityReport.state.instance = capabilityRequest.state.instance;
								capabilityReport.state.actionResult = new ActionResult();
								capabilityReport.state.actionResult.status = "DONE";
								deviceStateReport.capabilities.add(capabilityReport);
								sentAtLeastOne = true;
								break;
							}
						}
						
					}
				}
				
				if (!sentAtLeastOne) {
					Capability capabilityReport = new Capability();
					capabilityReport.type = capabilityRequest.type;
					capabilityReport.state = new State();
					capabilityReport.state.instance = capabilityRequest.state.instance;
					capabilityReport.state.actionResult = new ActionResult();
					capabilityReport.state.actionResult.status = "ERROR";
					capabilityReport.state.actionResult.errorCode = "INVALID_ACTION";
					capabilityReport.state.actionResult.errorMessage = "Свойство не поддерживается";
					deviceStateReport.capabilities.add(capabilityReport);
				}
				
			}
			
			
		} else {
			deviceStateReport.actionResult = new ActionResult();
			deviceStateReport.actionResult.status = "ERROR";
			deviceStateReport.actionResult.errorCode = "DEVICE_UNREACHABLE";
		}
		
		return deviceStateReport;
	}
	
}
