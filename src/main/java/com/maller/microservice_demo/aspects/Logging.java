package com.maller.microservice_demo.aspects;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class Logging {
	
	Logger logger;

	@Pointcut("execution(* com.maller.microservice_demo.controller.*.*(..))")
	private void controllerClassMethods() {
		//Pointcut notation
	}

	
	@Before("controllerClassMethods()")
	public void logBeforeMethodExecution(JoinPoint jp) {
		logger = LogManager.getLogger(jp.getClass());
		logger.log(Level.DEBUG, jp.getSignature().getName(), "Method execution started with parameters: ", jp.getArgs());
	}
	
	@AfterReturning("controllerClassMethods()")
	public void logAfterMethodExecution(JoinPoint jp, Object result) {
		logger = LogManager.getLogger(jp.getClass());
		logger.log(Level.DEBUG, jp.getSignature().getName(), "Method execution completed successfully");
	}
	
	@AfterThrowing("controllerClassMethods()")
	public void logAfterMethodThrows(JoinPoint jp, Throwable ex) {
		logger = LogManager.getLogger(jp.getClass());
		logger.log(Level.ERROR, jp.getSignature().getName(), "Method execution failed with error: ", ex.getMessage(),ex.getStackTrace());
	}
	
}
