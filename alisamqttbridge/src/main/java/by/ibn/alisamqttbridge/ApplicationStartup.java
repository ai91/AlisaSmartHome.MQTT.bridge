package by.ibn.alisamqttbridge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import by.ibn.alisamqttbridge.service.DeviceRepository;
import by.ibn.alisamqttbridge.service.MQTTService;
import by.ibn.alisamqttbridge.service.OutgoingDiscoveryService;

@Component
public class ApplicationStartup {

	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private OutgoingDiscoveryService outgoingDiscoveryService;
	
	@Autowired
	private MQTTService mqttService;

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {

		mqttService.subscribeOnTopics(deviceRepository);
		
		outgoingDiscoveryService.reportDiscovery();
		
	}
}