package by.ibn.alisamqttbridge.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import by.ibn.alisamqttbridge.resources.Response;

/**
 * Entry point liveness check
 *
 * @author Anar Ibragimoff
 *
 */
@RestController
public class LivenessController {

	private Logger log = LoggerFactory.getLogger(LivenessController.class);

	@RequestMapping(
			path = "/", 
			method = RequestMethod.HEAD)
	public ResponseEntity<Response> head() {

		log.trace("Processing request");

		return new ResponseEntity<Response>(HttpStatus.OK);
		
	}

}
