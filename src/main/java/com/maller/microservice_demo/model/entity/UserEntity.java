package com.maller.microservice_demo.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import lombok.Data;

@Entity
@Table(name = "user")
@Data
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
}
