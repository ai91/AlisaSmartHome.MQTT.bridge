package by.ibn.alisamqttbridge;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQTTConnectOptionsConfig {

	@Bean
	@ConfigurationProperties(prefix = "mqtt")
	public MqttConnectOptions mqttConnectOptions() {
		return new MqttConnectOptions();
	}	
}
