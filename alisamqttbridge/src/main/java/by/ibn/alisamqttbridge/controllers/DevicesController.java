package by.ibn.alisamqttbridge.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import by.ibn.alisamqttbridge.resources.Response;
import by.ibn.alisamqttbridge.service.IncomingDevicesService;

/**
 * Entry point for devices info
 *
 * @author Anar Ibragimoff
 *
 */
@RestController
public class DevicesController {

	private Logger log = LoggerFactory.getLogger(DevicesController.class);

	@Autowired
	private IncomingDevicesService service;

	@RequestMapping(
			path = "/user/devices", 
			method = RequestMethod.GET, 
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Response> getDevices(@RequestHeader("X-Request-Id") String requestId) {

		try {

			log.trace("Processing request {}", requestId);

			Response response = service.getDevices(requestId);

			return new ResponseEntity<Response>(response, HttpStatus.OK);
		} catch (Exception e) {

			log.error(e.toString(), e);

			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
