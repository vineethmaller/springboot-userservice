package com.maller.microservice_demo.controllers;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.maller.microservice_demo.model.daos.UserDAO;
import com.maller.microservice_demo.services.UserService;


@RestController
@RequestMapping("users/")
public class UserController {
	
	UserService userService;
	
	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	
	@GetMapping(path = "{id}", 
				consumes = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON }, 
				produces = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ResponseEntity<UserDAO> getUser(@PathVariable long id) {
		UserDAO user = userService.getUser(id);
		if(user.equals(new UserDAO()))
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<>(user, HttpStatus.FOUND);
	}

	
	@GetMapping(path = "",
				consumes = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON }, 
				produces = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ResponseEntity<List<UserDAO>> getUsers() {
		List<UserDAO> users = userService.getAllUsers();
		
		if(users == null)
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
		return new ResponseEntity<>(users, HttpStatus.FOUND);
	}
	
	
	@PostMapping(path = "",
				consumes = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON }, 
				produces = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ResponseEntity<UserDAO> createUser(@Valid @RequestBody UserDAO user) {
		String password = user.getEncryptedPassword();
		user.setEncryptedPassword(null);
		UserDAO retreivedUser;
		
		if(user.getId() != 0)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
		user.setEncryptedPassword(password);
		retreivedUser = userService.addUser(user);
		if(retreivedUser != null)
			return new ResponseEntity<>(retreivedUser, HttpStatus.CREATED);
		return new ResponseEntity<>(HttpStatus.CONFLICT);
	}
	
	
	@PutMapping(path = "",
				consumes = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON }, 
				produces = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ResponseEntity<UserDAO> updateUser(@Valid @RequestBody UserDAO user) {
		String password = user.getEncryptedPassword();
		user.setEncryptedPassword(null);
		if(user.getId() == 0) 
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
		UserDAO retreivedUser = userService.getUser(user.getId());
		if(retreivedUser.equals(new UserDAO()))
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
		user.setEncryptedPassword(password);
		retreivedUser = userService.addUser(user);
		if(retreivedUser == null)
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		return new ResponseEntity<>(retreivedUser, HttpStatus.ACCEPTED);
	}
	
	
	@DeleteMapping(path = "{id}", 
					consumes = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON }, 
					produces = { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public HttpStatus deleteUser(@PathVariable long id) {
		userService.removeUser(id);
		return HttpStatus.OK;
	}
	
	
	@GetMapping(path = "{id}/password", 
					consumes = { MediaType.APPLICATION_JSON }, 
					produces = { MediaType.APPLICATION_JSON })
	public ResponseEntity<String> getUserEncryptedPassword(@PathVariable long id) {
		String encryptedPassword = userService.getEncryptedPasswordById(id);
		
		if(encryptedPassword == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
		return new ResponseEntity<>(encryptedPassword, HttpStatus.FOUND);
	}
}
