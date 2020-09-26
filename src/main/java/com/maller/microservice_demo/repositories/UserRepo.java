package com.maller.microservice_demo.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.maller.microservice_demo.model.entities.UserEntity;

public interface UserRepo extends CrudRepository<UserEntity, Long> {

	@Query("select encryptedPassword from UserEntity where id = ?1")
	public String findEncryptedPasswordById(Long id);
	
}
