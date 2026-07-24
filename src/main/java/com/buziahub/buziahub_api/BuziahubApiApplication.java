package com.buziahub.buziahub_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BuziahubApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuziahubApiApplication.class, args);
	}

}
