package com.maller.microservice_demo.model.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;

	@Column(nullable=false)
	@Size(min=2, max=15)
	String firstName;
	
	@Column(nullable=false)
	@Size(min=2, max=15)
	String lastName;
	
	@Column(nullable=false, unique=true)
	@Email
	String email;
	
	String encryptedPassword;
	
	public UserEntity(UserEntity user) {
		this.id = user.getId(); 
		this.firstName = user.getFirstName(); 
		this.lastName = user.getLastName();
		this.email = user.getEmail();
		this.encryptedPassword = user.getEncryptedPassword();
	}
}
