package com.maller.microservice_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class UserMicroService {

	public static void main(String[] args) {
		SpringApplication.run(UserMicroService.class, args);
	}

}
