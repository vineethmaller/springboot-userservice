package com.maller.microservice_demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

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
import org.springframework.security.crypto.password.PasswordEncoder;
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
	
	@Autowired
	PasswordEncoder encoder;
	
	UserService userService;
	
	Map<Long, UserDAO> userDAOList;
	Map<Long, UserEntity> userEntityList;
	
	@BeforeEach
	void initialize() {
		userService = new UserService(userRepo, mapper, encoder);
		
		userDAOList = new TreeMap<>();
		userEntityList = new TreeMap<>();
		
		userDAOList.put((long)1, new UserDAO(1, "Vineeth", "Maller", "vinnethmaller@mgail.com", null));
		userDAOList.put((long)2, new UserDAO(2, "Divya", "Pai", "divyapai@gmail.com", null));
		userDAOList.put((long)3, new UserDAO(3, "Sankalp", "Gupta", "sankalpgupta@gmail.com", null));
		
		for(long i=1;i<4;i++)
			userEntityList.put(i, mapper.map(userDAOList.get(i), UserEntity.class));
	}


	@Test
	void shouldReturnEmptyUserIfUserDoesNotExist() {
		long userId = 1;
		UserDAO result;
		Optional<UserEntity> nullUser = Optional.ofNullable(new UserEntity());
		
		UserDAO expected = mapper.map(nullUser, UserDAO.class);
		
		Mockito.when(userRepo.findById(userId)).thenReturn(nullUser);
		result = userService.getUser(userId);
		
		Assert.assertEquals("Returned user is not empty", expected, result);
	}
	
	@Test
	void shouldReturnValidUserIfUserExists() {
		long userId = 2;
		UserDAO result;
		Optional<UserEntity> userEntity = Optional.ofNullable(userEntityList.get(userId));
		UserDAO expected = userDAOList.get(userId);
		
		Mockito.when(userRepo.findById(userId)).thenReturn(userEntity);
		result = userService.getUser(userId);
		
		Assert.assertEquals("Retrieved user does not match expected", expected, result);
	}
	
	@Test
	void shouldReturnEmptyUserListIfUsersDoNotExist() {
		List<UserDAO> result = new ArrayList<>();
		
		userEntityList.clear();
		
		Mockito.when(userRepo.findAll()).thenReturn(List.copyOf(userEntityList.values()));
		result = userService.getAllUsers();
		
		Assert.assertEquals("Non empty list returned", 0,result.size());
	}
	
	@Test
	void shouldReturnAllUsersIfUsersDoExist() {
		List<UserDAO> result = new ArrayList<>();
		
		Mockito.when(userRepo.findAll()).thenReturn(List.copyOf(userEntityList.values()));
		result = userService.getAllUsers();
		
		Assert.assertEquals("Returned list does not match expected", List.copyOf(userDAOList.values()), result);
	}
	
	@Test
	void shouldSaveUserIfUserProvided() {
		UserDAO result;
		UserDAO inputUser = new UserDAO(4, "Will", "Smith", "me@willsmith.com", "password");
		
		UserEntity savedUserEntity = mapper.map(inputUser, UserEntity.class);
		savedUserEntity.setEncryptedPassword(encoder.encode(inputUser.getEncryptedPassword()));
		
		UserDAO expected = new UserDAO(inputUser);
		expected.setEncryptedPassword(null);
		
		Mockito.lenient().when(userRepo.save(Mockito.any(UserEntity.class))).thenReturn(savedUserEntity);
		result = userService.addUser(inputUser);
		
		Assert.assertNull("Encrypted password field in output object is not null", result.getEncryptedPassword());
		Assert.assertEquals("Returned user does not match expected", expected, result);
	}
	
}
