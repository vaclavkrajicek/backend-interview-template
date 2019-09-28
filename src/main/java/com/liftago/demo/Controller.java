package com.liftago.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Minimalistic implementation of the technical task. Does not include any
 * additional functionality above specification (e.g. GET, PUT, DELETE,
 * user-centric entity, scope, email format validation, capacity checking,
 * etc.).
 */
@RestController
@RequestMapping(path = "/emails")
public class Controller {

	@Autowired
	private EmailsRepository emailsRepository;

	/**
	 * Implements business logic while inserting email and name. It returns relevant
	 * HTTP codes (201 when created, 409 if there is conflict with previously
	 * inserted record) together with response object (one thing above
	 * specification).
	 * 
	 * @return Controller response object wrapped in response entity.
	 */
	@PostMapping
	public ResponseEntity<ControllerPostResponse> post(@RequestBody ControllerPostRequest controllerPostRequest) {
		if (emailsRepository.hasEmail(controllerPostRequest.getEmail())) {
			return new ResponseEntity<ControllerPostResponse>(new ControllerPostResponse(false), null,
					HttpStatus.CONFLICT);
		} else {
			emailsRepository.storeEmail(controllerPostRequest.getEmail(), controllerPostRequest.getName());
			return new ResponseEntity<ControllerPostResponse>(new ControllerPostResponse(true), null,
					HttpStatus.CREATED);
		}
	}
}
