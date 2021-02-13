package by.ibn.alisamqttbridge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import by.ibn.alisamqttbridge.service.DeviceRepository;
import by.ibn.alisamqttbridge.service.MQTTService;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private MQTTService mqttService;

	@Override
	public void onApplicationEvent(final ApplicationReadyEvent event) {

		mqttService.subscribeOnTopics(deviceRepository);
		
	}
}