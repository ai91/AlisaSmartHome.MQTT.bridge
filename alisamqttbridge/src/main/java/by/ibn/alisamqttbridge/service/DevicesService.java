package by.ibn.alisamqttbridge.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.ibn.alisamqttbridge.model.Device;
import by.ibn.alisamqttbridge.resources.Payload;
import by.ibn.alisamqttbridge.resources.Response;

@Service
public class DevicesService {

	@Autowired
	private DeviceRepository devicesRepository;

	public Response getDevices() {
		
		Response response = new Response();
		
		response.requestId = UUID.randomUUID().toString();
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
