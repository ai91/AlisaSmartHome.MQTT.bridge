package by.ibn.alisamqttbridge.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.ibn.alisamqttbridge.resources.Payload;
import by.ibn.alisamqttbridge.resources.Request;
import by.ibn.alisamqttbridge.resources.Response;

@Service
public class IncomingQueryService {
	
	@Autowired
	private DeviceStateService deviceStateService;

	public Response getStates(Request request, String requestId) {
		
		Response response = new Response();
		response.requestId = requestId;
		
		response.payload = new Payload();
		
		if (request.devices != null) {
			
			response.payload.devices = request.devices.stream()
					.map( device -> device.id )
					.map( deviceId -> deviceStateService.getDeviceState(deviceId) )
					.collect(Collectors.toList());
			
		}
				
		return response;
	}

}
