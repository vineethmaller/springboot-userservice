package com.maller.microservice_demo.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.maller.microservice_demo.model.entity.UserEntity;

public interface UserRepo extends CrudRepository<UserEntity, Long> {

	public Optional<String> findEncryptedPasswordById(Long id);
	
}
