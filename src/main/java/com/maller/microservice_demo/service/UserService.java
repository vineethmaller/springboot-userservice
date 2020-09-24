package com.maller.microservice_demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.maller.microservice_demo.model.dao.UserDAO;
import com.maller.microservice_demo.model.entity.UserEntity;
import com.maller.microservice_demo.repository.UserRepo;

@Service
public class UserService {
	
	UserRepo userRepo;
	ModelMapper mapper;
	
	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	public UserService(UserRepo userRepo, ModelMapper mapper) {
		this.userRepo = userRepo;
		this.mapper = mapper;
	}
	
	public UserDAO getUser(long id) {
		UserEntity userEntity = userRepo.findById(id).orElse(new UserEntity());
		return mapper.map(userEntity, UserDAO.class);
	}
	
	public List<UserDAO> getAllUsers() {
		List<UserEntity> users = (List<UserEntity>) userRepo.findAll();
		return users.stream().map(user -> mapper.map(user, UserDAO.class)).collect(Collectors.toList());
	}
	
	public UserDAO addUser(UserDAO userDAO) {
		try {
			String encryptedPassword = encoder.encode(userDAO.getEncryptedPassword());
			userDAO.setEncryptedPassword(encryptedPassword);
		
			UserEntity userEntity = mapper.map(userDAO, UserEntity.class);
			userEntity = userRepo.save(userEntity);
		
			return mapper.map(userEntity, UserDAO.class);
		} catch(Exception ex) {
			return null;
		}
	}
	
	public void removeUser(long id) {
		if(userRepo.findById(id).orElse(null) != null)
			userRepo.deleteById(id);
	}

}
