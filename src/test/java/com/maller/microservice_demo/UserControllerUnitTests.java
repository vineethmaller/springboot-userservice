package com.maller.microservice_demo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maller.microservice_demo.configurations.CommonConfig;
import com.maller.microservice_demo.configurations.SecurityConfig;
import com.maller.microservice_demo.model.daos.UserDAO;
import com.maller.microservice_demo.services.UserService;

@SpringBootTest
@ContextConfiguration(classes = { CommonConfig.class, SecurityConfig.class})
@RunWith(SpringRunner.class)
class UserControllerUnitTests {

	
	private static ObjectMapper mapper;
	private static final String URI = "/users";
	
	MockMvc mvc;
	
	@Autowired
	WebApplicationContext webAppContext;
	
	@Mock
	UserService userService;
	
	MvcResult mvcResult;
	Map<Long, UserDAO> userList;
	
	@BeforeAll
	static void setUp() {
		mapper = new ObjectMapper();
	}
	
	@BeforeEach
	void initialize() throws Exception {
		mvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
		userList = new TreeMap<>();
		
		userList.put((long) 1, new UserDAO(0, "Tom", "Hanks", "wilson@houston.com", "lifeIsLikeABoxOfChocolates"));
		userList.put((long) 2, new UserDAO(0, "James", "Bond", "worstspyinhistory@mi6.uk.gov", "BondJamesBond"));
		userList.put((long) 3, new UserDAO(0, "Matthew", "McConaughey", "ihaveanoscar@texan.com", "allRightAllRightAllRight"));
		
		for(int index=1; index<4; index++)
			shouldReturnStatusCreatedIfValidUserPassedForPostUser(index);
	}
	
	
	@Test
	void shouldReturnStatusFoundIfUserExistsForGetUser() throws Exception {
		String getURI = URI + "/1";
		int expectedStatus = HttpStatus.FOUND.value();
		
		Mockito.when(userService.getUser(Mockito.any(Long.class))).thenReturn(userList.get((long) 1));
		
		mvcResult = mvc.perform(MockMvcRequestBuilders.get(getURI)
				.accept(MediaType.APPLICATION_JSON)).andReturn();
		
		int actualStatus = mvcResult.getResponse().getStatus();
		
		Assert.assertEquals("Response status should be FOUND", expectedStatus, actualStatus);
	}
	
	@Test
	void shouldReturnStatusNotFoundIfUserNotExistsForGetUser() throws Exception {
		String getURI = URI + "/4";
		int expectedStatus = HttpStatus.NOT_FOUND.value();
		
		Mockito.when(userService.getUser(Mockito.anyLong())).thenReturn(new UserDAO());
		
		mvcResult = mvc.perform(MockMvcRequestBuilders.get(getURI)
				.accept(MediaType.APPLICATION_JSON)).andReturn();
		
		int actualStatus = mvcResult.getResponse().getStatus();
		
		Assert.assertEquals("Response status should be NOT FOUND", expectedStatus, actualStatus);
	}

	@Test
	void shouldReturnStatusFoundIfUsersExistForGetUsers() throws Exception {
		int expectedStatus = HttpStatus.FOUND.value();
		
		Mockito.when(userService.getAllUsers()).thenReturn(List.copyOf(userList.values()));
		
		mvcResult = mvc.perform(MockMvcRequestBuilders.get(URI)
				.accept(MediaType.APPLICATION_JSON)).andReturn();
		
		int actualStatus = mvcResult.getResponse().getStatus();
		
		Assert.assertEquals("Response status should be FOUND", expectedStatus, actualStatus);
	}
	
	@Test
	void shouldReturnStatusNoContentIfNoUsersExistForGetUsers() throws Exception {
		int expectedStatus = HttpStatus.NO_CONTENT.value();
		
		Mockito.when(userService.getAllUsers()).thenReturn(null);
		
		mvcResult = mvc.perform(MockMvcRequestBuilders.get(URI)
				.accept(MediaType.APPLICATION_JSON)).andReturn();
		
		int actualStatus = mvcResult.getResponse().getStatus();
		
		Assert.assertEquals("Response status should be FOUND", expectedStatus, actualStatus);
	}

	
	void shouldReturnStatusCreatedIfValidUserPassedForPostUser(long index) throws Exception {
		int expectedStatus = HttpStatus.CREATED.value();
		UserDAO returnUser;
		
		UserDAO user = userList.get(index);
		userList.remove(index);
		String jsonContent = mapper.writeValueAsString(user);
		
		user.setId(index);
		user.setEncryptedPassword(null);
		
		Mockito.when(userService.addUser(Mockito.any())).thenReturn(user);
		
		mvcResult = mvc.perform(MockMvcRequestBuilders.post(URI)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent)).andReturn();
		
		//Mockito.verify(userService, Mockito.times(1)).addUser(Mockito.any());
		
		int actualStatus = mvcResult.getResponse().getStatus();
		Assert.assertEquals("Response status should be CREATED", expectedStatus, actualStatus);
		
		jsonContent = mvcResult.getResponse().getContentAsString();
		returnUser = mapper.readValue(jsonContent, UserDAO.class);
		
		Assert.assertEquals("EncryptedPassword should not be returned", null, returnUser.getEncryptedPassword());
	}
	
	@Test
	void shouldReturnStatusBadRequestIfUserIDPassedIsNotZeroForPostUser() throws Exception {
		int expectedStatus = HttpStatus.BAD_REQUEST.value();
		
		UserDAO user = new UserDAO(2, "Will", "Smith", "me@willsmith.com", "password");
		String jsonContent = mapper.writeValueAsString(user);
		
		mvcResult = mvc.perform(MockMvcRequestBuilders.post(URI)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent)).andReturn();
		
		Mockito.verify(userService, Mockito.times(0)).addUser(Mockito.any());
		
		int actualStatus = mvcResult.getResponse().getStatus();
		Assert.assertEquals("Response status should be BAD REQUEST", expectedStatus, actualStatus);
	}
	
	@Test
	void shouldReturnStatusConflictIfUserDetailsPassedIsDuplicateForPostUser() throws Exception {
		int expectedStatus = HttpStatus.CONFLICT.value();
		
		UserDAO user = new UserDAO(0, "James", "Bond", "worstspyinhistory@mi6.uk.gov", "BondJamesBond");
		String jsonContent = mapper.writeValueAsString(user);
		
		mvcResult = mvc.perform(MockMvcRequestBuilders.post(URI)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent)).andReturn();
		
		Mockito.verify(userService, Mockito.times(1)).addUser(Mockito.any());
		
		int actualStatus = mvcResult.getResponse().getStatus();
		Assert.assertEquals("Response status should be CONFLICT", expectedStatus, actualStatus);
	}
	
	@Test
	void shouldReturnStatusAcceptedIfValidUserPassedForPutUser() throws Exception {
		int expectedStatus = HttpStatus.ACCEPTED.value();
		String updatedDetail = "idriselba@mi6.uk.gov";
		UserDAO returnUser;
		
		UserDAO user = new UserDAO(2, "James", "Bond", updatedDetail, "BondJamesBond");
		String jsonContent = mapper.writeValueAsString(user);
		
		user.setEncryptedPassword(null);
		Mockito.when(userService.getUser(Mockito.anyLong())).thenReturn(userList.get((long) 2));
		Mockito.when(userService.addUser(Mockito.any(UserDAO.class))).thenReturn(user);
		
		mvcResult = mvc.perform(MockMvcRequestBuilders.put(URI)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent)).andReturn();
		
		Mockito.verify(userService, Mockito.times(1)).addUser(Mockito.any(UserDAO.class));
		
		int actualStatus = mvcResult.getResponse().getStatus();
		Assert.assertEquals("Response status should be ACCEPTED", expectedStatus, actualStatus);
		
		jsonContent = mvcResult.getResponse().getContentAsString();
		returnUser = mapper.readValue(jsonContent, UserDAO.class);
		
		Assert.assertEquals("Updated detail is not reflected", updatedDetail, returnUser.getEmail());
	}
	
	@Test
	void shouldReturnStatusBadRequestIfUserIDPassedIsZeroForPutUser() throws Exception {
		int expectedStatus = HttpStatus.BAD_REQUEST.value();
		
		UserDAO user = new UserDAO(0, "James", "Bond", "idriselba@mi6.uk.gov", "BondJamesBond");
		String jsonContent = mapper.writeValueAsString(user);
		
		mvcResult = mvc.perform(MockMvcRequestBuilders.put(URI)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent)).andReturn();
		
		Mockito.verify(userService, Mockito.times(0)).getUser(Mockito.anyLong());
		Mockito.verify(userService, Mockito.times(0)).addUser(Mockito.any(UserDAO.class));
		
		int actualStatus = mvcResult.getResponse().getStatus();
		Assert.assertEquals("Response status should be BAD REQUEST", expectedStatus, actualStatus);
	}
	
	@Test
	void shouldReturnStatusNotFoundIfUserIDPassedNotExistsForPutUser() throws Exception {
		int expectedStatus = HttpStatus.NOT_FOUND.value();
		
		UserDAO user = new UserDAO(4, "John", "Doe", "johndoe@gmail.com", "password");
		String jsonContent = mapper.writeValueAsString(user);
		
		Mockito.when(userService.getUser(Mockito.anyLong())).thenReturn(new UserDAO());
		
		mvcResult = mvc.perform(MockMvcRequestBuilders.put(URI)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent)).andReturn();
		
		int actualStatus = mvcResult.getResponse().getStatus();
		Assert.assertEquals("Response status should be NOT FOUND", expectedStatus, actualStatus);
	}
	
	@Test
	void shouldReturnStatusConflictIfUserDetailsPassedIsDuplicateForPutUser() throws Exception {
		int expectedStatus = HttpStatus.CONFLICT.value();
		
		UserDAO user = new UserDAO(2, "James", "Bond", "idriselba@mi6.uk.gov", "BondJamesBond");
		String jsonContent = mapper.writeValueAsString(user);
		
		user.setEncryptedPassword(null);
		Mockito.when(userService.getUser(Mockito.anyLong())).thenReturn(userList.get((long) 2));
		Mockito.when(userService.addUser(Mockito.any(UserDAO.class))).thenReturn(null);
		
		mvcResult = mvc.perform(MockMvcRequestBuilders.put(URI)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonContent)).andReturn();
		
		Mockito.verify(userService, Mockito.times(1)).addUser(Mockito.any(UserDAO.class));
		
		int actualStatus = mvcResult.getResponse().getStatus();
		Assert.assertEquals("Response status should be CONFLICT", expectedStatus, actualStatus);
	}
	
	@Test
	void shouldReturnStatusNotFoundForGetUserIfExistingUserIDPassedForDeleteUser() throws Exception {
		String uri = URI + "/2";
		int expectedStatus = HttpStatus.OK.value();
		
		Mockito.doNothing().when(userService).removeUser(Mockito.anyLong());
		
		mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)
				.accept(MediaType.APPLICATION_JSON)).andReturn();
		
		int actualStatus = mvcResult.getResponse().getStatus();
		Assert.assertEquals("Response status should be OK", expectedStatus, actualStatus);
		userList.remove((long) 2);
		
		expectedStatus = HttpStatus.NOT_FOUND.value();
		
		Mockito.when(userService.getUser(Mockito.anyLong())).thenReturn(new UserDAO());
		mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON)).andReturn();
		
		actualStatus = mvcResult.getResponse().getStatus();
		Assert.assertEquals("Response status should be NOT FOUND", expectedStatus, actualStatus);
	}
	
	@Test
	void shouldReturnSameUserListForGetUsersIfNonExistingUserIDPassedForDeleteUser() throws Exception {
		String jsonContent;
		String uri = URI + "/2";
		int actualStatus;
		int expectedStatus;
		List<UserDAO> expectedUsers;
		List<UserDAO> returnedUsers;
		
		//Fetch Users before delete
		expectedStatus = HttpStatus.FOUND.value();
		
		Mockito.when(userService.getAllUsers()).thenReturn(List.copyOf(userList.values()));
		
		mvcResult = mvc.perform(MockMvcRequestBuilders.get(URI)
				.accept(MediaType.APPLICATION_JSON)).andReturn();
		
		actualStatus = mvcResult.getResponse().getStatus();
		Assert.assertEquals("Response status should be FOUND", expectedStatus, actualStatus);
		
		jsonContent = mvcResult.getResponse().getContentAsString();
		expectedUsers = mapper.readValue(jsonContent, new TypeReference<List<UserDAO>>() {});
		
		//Try to delete non existing User
		expectedStatus = HttpStatus.OK.value();
		
		Mockito.doNothing().when(userService).removeUser(Mockito.anyLong());
		
		mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)
				.accept(MediaType.APPLICATION_JSON)).andReturn();
		
		actualStatus = mvcResult.getResponse().getStatus();
		Assert.assertEquals("Response status should be OK", expectedStatus, actualStatus);
		
		//Fetch Users after delete
		expectedStatus = HttpStatus.FOUND.value();
		
		mvcResult = mvc.perform(MockMvcRequestBuilders.get(URI)
				.accept(MediaType.APPLICATION_JSON)).andReturn();
		
		actualStatus = mvcResult.getResponse().getStatus();
		Assert.assertEquals("Response status should be FOUND", expectedStatus, actualStatus);
		
		jsonContent = mvcResult.getResponse().getContentAsString();
		returnedUsers = mapper.readValue(jsonContent, new TypeReference<List<UserDAO>>() {});
		
		Assert.assertEquals("User list is not same", expectedUsers, returnedUsers);
	}
	
	@Test
	void shouldReturnValidEncryptedPasswordIfUserExistsForGetEncryptedPassword() throws Exception {
		String getURI = URI + "/3/password";
		int expectedStatus = HttpStatus.FOUND.value();
		
		mvcResult = mvc.perform(MockMvcRequestBuilders.get(getURI)
				.accept(MediaType.APPLICATION_JSON)).andReturn();
		
		int actualStatus = mvcResult.getResponse().getStatus();
		Assert.assertEquals("Response status should be FOUND", expectedStatus, actualStatus);
		
		
	}
}
