package by.ibn.alisamqttbridge.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import by.ibn.alisamqttbridge.resources.Response;
import by.ibn.alisamqttbridge.service.DevicesService;

/**
 * Entry point for devices info
 *
 * @author Anar Ibragimoff
 *
 */
@RestController
public class DevicesController {

	Logger log = LoggerFactory.getLogger(DevicesController.class);

	@Autowired
	private DevicesService service;

	@RequestMapping(
			path = "/user/devices", 
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> getDevices() {

		try {

			log.trace("Processing request");

			Response response = service.getDevices();

			return new ResponseEntity<Response>(response, HttpStatus.OK);
		} catch (Exception e) {

			log.error(e.toString(), e);

			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
