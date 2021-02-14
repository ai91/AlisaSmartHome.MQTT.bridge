package by.ibn.alisamqttbridge.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.ibn.alisamqttbridge.resources.Payload;
import by.ibn.alisamqttbridge.resources.Response;

@Service
public class DevicesService {

	@Autowired
	private DeviceRepository devicesRepository;

	public Response getDevices(String requestId) {
		
		Response response = new Response();
		
		response.requestId = requestId;
		response.payload = new Payload();
		try {
			response.payload.userId = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			response.payload.userId = UUID.randomUUID().toString();
		}
		
		response.payload.devices = devicesRepository.getDeviceResources();
		
		return response;
	}

	
	
}
