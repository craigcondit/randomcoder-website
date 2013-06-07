package org.randomcoder.config;

import javax.inject.Inject;

import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@SuppressWarnings("javadoc")
@EnableTransactionManagement
//@ImportResource({"classpath:spring-security.xml"})
//@Import({ DatabaseConfig.class, TwitterConfig.class })
//@ComponentScan({"org.randomcoder.craigandanne.bo"})
public class RootContext
{
	@Inject
	Environment env;
}