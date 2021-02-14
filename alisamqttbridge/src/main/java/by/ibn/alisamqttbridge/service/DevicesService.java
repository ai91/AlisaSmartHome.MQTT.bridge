package by.ibn.alisamqttbridge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import by.ibn.alisamqttbridge.resources.Payload;
import by.ibn.alisamqttbridge.resources.Response;

@Service
public class DevicesService {

	@Autowired
	private DeviceRepository devicesRepository;
	
	@Value("${devices.payload.userId:66211404}")
	private String userId;

	public Response getDevices(String requestId) {
		
		Response response = new Response();
		
		response.requestId = requestId;
		response.payload = new Payload();
		response.payload.userId = userId;
		
		response.payload.devices = devicesRepository.getDeviceResources();
		
		return response;
	}

	
	
}
