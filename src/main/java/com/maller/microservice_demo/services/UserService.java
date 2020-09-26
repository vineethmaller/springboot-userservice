package com.maller.microservice_demo.services;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.maller.microservice_demo.model.daos.UserDAO;
import com.maller.microservice_demo.model.entities.UserEntity;
import com.maller.microservice_demo.repositories.UserRepo;

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
		UserDAO userDAO = mapper.map(userEntity, UserDAO.class);
		userDAO.setEncryptedPassword(null);
		return userDAO;
	}
	
	public List<UserDAO> getAllUsers() {
		List<UserEntity> userEntityList = (List<UserEntity>) userRepo.findAll();
		List<UserDAO> users = userEntityList.stream().map(user -> mapper.map(user, UserDAO.class)).collect(Collectors.toList());
		users.stream().forEach(user -> user.setEncryptedPassword(null));
		return users;
	}
	
	public UserDAO addUser(UserDAO userDAO) {
		try {
			String encryptedPassword = encoder.encode(userDAO.getEncryptedPassword());
			userDAO.setEncryptedPassword(encryptedPassword);
		
			UserEntity userEntity = mapper.map(userDAO, UserEntity.class);
			userEntity = userRepo.save(userEntity);
		
			userDAO = mapper.map(userEntity, UserDAO.class);
			userDAO.setEncryptedPassword(null);
			return userDAO;
		} catch(Exception ex) {
			return null;
		}
	}
	
	public void removeUser(long id) {
		if(userRepo.findById(id).orElse(null) != null)
			userRepo.deleteById(id);
	}

	
	public String getEncryptedPasswordById(long id) {
		return userRepo.findEncryptedPasswordById(id);
	}
}
