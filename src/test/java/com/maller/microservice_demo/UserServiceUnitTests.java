package com.maller.microservice_demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.maller.microservice_demo.configurations.CommonConfig;
import com.maller.microservice_demo.configurations.SecurityConfig;
import com.maller.microservice_demo.model.daos.UserDAO;
import com.maller.microservice_demo.model.entities.UserEntity;
import com.maller.microservice_demo.repositories.UserRepo;
import com.maller.microservice_demo.services.UserService;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = { CommonConfig.class, SecurityConfig.class })
@SpringBootTest
class UserServiceUnitTests {
	
	@Mock
	UserRepo userRepo;
	
	@Autowired
	ModelMapper mapper;
	
	UserService userService;
	
	List<UserDAO> userDAOList;
	List<UserEntity> userEntityList;
	
	@BeforeEach
	void initialize() {
		userService = new UserService(userRepo, mapper);
		
		userDAOList = new ArrayList<>();
		userEntityList = new ArrayList<>();
		
		userDAOList.add(new UserDAO(1, "Vineeth", "Maller", "vinnethmaller@mgail.com", null));
		userDAOList.add(new UserDAO(2, "Divya", "Pai", "divyapai@gmail.com", null));
		userDAOList.add(new UserDAO(3, "Sankalp", "Gupta", "sankalpgupta@gmail.com", null));
		
		userEntityList.addAll(userDAOList.stream().map(user -> mapper.map(user, UserEntity.class)).collect(Collectors.toList()));
	}

	
	@Test
	void shouldReturnEmptyUserIfUserDoesNotExist() {
		long userId = 1;
		UserDAO result;
		Optional<UserEntity> nullUser = Optional.of(new UserEntity());
		
		UserDAO expected = mapper.map(nullUser, UserDAO.class);
		
		Mockito.when(userRepo.findById(userId)).thenReturn(nullUser);
		result = userService.getUser(userId);
		
		Assert.assertEquals("Returned user is not empty", expected, result);
	}
	
	@Test
	void shouldReturnValidUserIfUserExists() {
		long userId = 2;
		UserDAO result;
		Optional<UserEntity> userEntity = Optional.of(userEntityList.get((int) (userId-1)));
		UserDAO expected = userDAOList.get((int) (userId-1));
		
		Mockito.when(userRepo.findById(userId)).thenReturn(userEntity);
		result = userService.getUser(userId);
		
		Assert.assertEquals("Retrived user does not match expected", expected, result);
	}
	
	@Test
	void shouldReturnEmptyUserListIfUsersDoNotExist() {
		List<UserDAO> result = new ArrayList<>();
		
		userEntityList.clear();
		
		Mockito.when(userRepo.findAll()).thenReturn(userEntityList);
		result = userService.getAllUsers();
		
		Assert.assertEquals("Non empty list returned", 0,result.size());
	}
}
