package com.maller.microservice_demo.repository;

import org.springframework.data.repository.CrudRepository;

import com.maller.microservice_demo.model.entity.UserEntity;

public interface UserRepo extends CrudRepository<UserEntity, Long> {

}
