package com.maller.microservice_demo.model.dao;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDAO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	long id;

	@Size(min=2, max=15)
	String firstName;
	
	@Size(min=2, max=15)
	String lastName;
	
	@Email
	String email;
	
	String encryptedPassword;
}
