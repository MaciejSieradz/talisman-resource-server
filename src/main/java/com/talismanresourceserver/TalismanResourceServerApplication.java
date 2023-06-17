package com.talismanresourceserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
public class TalismanResourceServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TalismanResourceServerApplication.class, args);
	}

}
