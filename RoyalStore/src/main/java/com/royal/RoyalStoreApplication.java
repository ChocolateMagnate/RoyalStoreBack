package com.royal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@Import(JacksonConfiguration.class)
public class RoyalStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoyalStoreApplication.class, args);
	}

}
