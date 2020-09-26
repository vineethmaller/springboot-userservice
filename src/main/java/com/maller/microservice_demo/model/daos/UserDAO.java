package com.maller.microservice_demo.model.daos;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserDAO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	long id;

	@Size(min=2, max=15)
	String firstName;
	
	@Size(min=2, max=15)
	String lastName;
	
	@Email
	String email;
	
	transient String encryptedPassword;
	
	@Override
	public String toString() {
		StringBuilder data = new StringBuilder();
		data.append("{ id : " + id);
		data.append(", firstName : " + firstName);
		data.append(", lastName : " + lastName);
		data.append(", email: " + email);
		data.append(" }");
		
		return data.toString();
	}
	
}
