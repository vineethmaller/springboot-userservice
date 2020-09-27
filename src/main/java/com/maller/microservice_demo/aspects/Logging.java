package com.maller.microservice_demo.aspects;

import java.util.Arrays;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.stereotype.Component;

@Aspect
@Component
public class Logging {
	
	Logger logger;

	@Pointcut("execution(* com.maller.microservice_demo.controllers.*.*(..))")
	private void controllerClassMethods() {
		//Point-cut notation
	}

	
	@Before("controllerClassMethods()")
	public void logBeforeMethodExecution(JoinPoint jp) {
		logger = LogManager.getLogger(jp.getClass());
		String message = String.format("Method: %1$s - Parameters: %2$s", 
											jp.getSignature().getName(), Arrays.toString(jp.getArgs()));
		logger.log(Level.INFO, message);
	}
	
	@AfterReturning(pointcut = "controllerClassMethods()", returning = "result")
	public void logAfterMethodExecution(JoinPoint jp, Object result) {
		logger = LogManager.getLogger(jp.getClass());
		String message = String.format("Method: %1$s - Status: SUCCESS - Return: %2$s", jp.getSignature().getName(), result);
		logger.log(Level.INFO, message);
	}
	
	@AfterThrowing(pointcut = "controllerClassMethods()", throwing = "ex")
	public void logAfterMethodThrows(JoinPoint jp, Throwable ex) {
		logger = LogManager.getLogger(jp.getClass());
		String message = String.format("%1$s - Method execution failed with error: %2$s %n %3$s", 
											jp.getSignature().getName(), ex.getMessage(),Arrays.toString(ex.getStackTrace()));
		logger.log(Level.ERROR, message);
	}
	
}
