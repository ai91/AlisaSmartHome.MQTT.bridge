package by.ibn.alisamqttbridge.service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.ibn.alisamqttbridge.model.ValueMap;
import by.ibn.alisamqttbridge.resources.ActionResult;
import by.ibn.alisamqttbridge.resources.Capability;
import by.ibn.alisamqttbridge.resources.Device;
import by.ibn.alisamqttbridge.resources.DeviceBridgingRule;
import by.ibn.alisamqttbridge.resources.Payload;
import by.ibn.alisamqttbridge.resources.Request;
import by.ibn.alisamqttbridge.resources.Response;
import by.ibn.alisamqttbridge.resources.State;

@Service
public class IncomingActionService {

	private Logger log = LoggerFactory.getLogger(IncomingActionService.class);
	
	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private MQTTService mqttService;
	
	public Response doAction(Request request, String requestId) {
		
		Response response = new Response();
		response.requestId = requestId;
		
		response.payload = new Payload();
		
		if (request.payload != null && request.payload.devices != null) {
			
			response.payload.devices = request.payload.devices.stream()
					.map( device -> doAction(device) )
					.collect(Collectors.toList());
			
		}
				
		return response;
		
	}

	private Device doAction(Device deviceRequest) {
		
		Device deviceStateReport = new Device();
		deviceStateReport.id = deviceRequest.id;
		
		log.trace("  performing action on device: {}", deviceRequest.id);
		
		Optional<Device> deviceOpt = deviceRepository.getDeviceById(deviceRequest.id);
		if (deviceOpt.isPresent()) {

			boolean sentAtLeastOne = false;
			Device device = deviceOpt.get();
			
			for (Capability capabilityRequest: deviceRequest.capabilities) {
				
				String typeRequest = capabilityRequest.type;
				String instanceRequest = capabilityRequest.state == null ? null : capabilityRequest.state.instance;
				
				for (Capability capability: device.capabilities) {
					
					if (StringUtils.equals(capability.type, typeRequest)) {
					
						for (DeviceBridgingRule rule: capability.rules) {
						
							if (StringUtils.equals(instanceRequest, rule.alisa.instance) &&
									rule.mqtt != null && StringUtils.isNotBlank(rule.mqtt.commands)
									&& capabilityRequest.state.value != null) {
								
								String alisaValue = capabilityRequest.state.value.toString();
								String mqttValue = alisaValue;
							
								for (ValueMap valueMap: rule.valueMapsToMqtt) {
									if (valueMap.isApplicable(alisaValue)) {
										mqttValue = valueMap.map(alisaValue);
										mqttService.sendMessage(rule.mqtt.commands, mqttValue);
										
										Capability capabilityReport = new Capability();
										capabilityReport.type = typeRequest;
										capabilityReport.state = new State();
										capabilityReport.state.instance = instanceRequest;
										capabilityReport.state.actionResult = new ActionResult();
										capabilityReport.state.actionResult.status = "DONE";
										if (deviceStateReport.capabilities == null) {
											deviceStateReport.capabilities = new ArrayList<>();
										}
										deviceStateReport.capabilities.add(capabilityReport);
										sentAtLeastOne = true;
										log.trace("  successfully sent message: {} -> {}", mqttValue, rule.mqtt.commands);
										break;
									}
								}
								
							} else {
								log.trace("  device found, capability found, but rule doesn't match");
							}
						}
					}
					
				}
				
				if (!sentAtLeastOne) {
					log.warn("  device found, but capability is not supported or value didn't match. Device: {}, capability: {}, instance: {}, value: {}", device.id, capabilityRequest.type, capabilityRequest.state.instance, capabilityRequest.state.value.toString());
					Capability capabilityReport = new Capability();
					capabilityReport.type = capabilityRequest.type;
					capabilityReport.state = new State();
					capabilityReport.state.instance = capabilityRequest.state.instance;
					capabilityReport.state.actionResult = new ActionResult();
					capabilityReport.state.actionResult.status = "ERROR";
					capabilityReport.state.actionResult.errorCode = "INVALID_ACTION";
					capabilityReport.state.actionResult.errorMessage = "Свойство не поддерживается";
					if (deviceStateReport.capabilities == null) {
						deviceStateReport.capabilities = new ArrayList<>();
					}
					deviceStateReport.capabilities.add(capabilityReport);
				}
				
			}
			
			
		} else {
			log.warn("  device not found: {}", deviceRequest.id);
			deviceStateReport.actionResult = new ActionResult();
			deviceStateReport.actionResult.status = "ERROR";
			deviceStateReport.actionResult.errorCode = "DEVICE_UNREACHABLE";
		}
		
		return deviceStateReport;
	}
	
}
