package com.nextClass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NextClassApplication {

	public static void main(String[] args) {
		SpringApplication.run(NextClassApplication.class, args);
	}

}
