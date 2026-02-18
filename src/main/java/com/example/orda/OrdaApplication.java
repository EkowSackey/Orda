package com.example.orda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OrdaApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdaApplication.class, args);

	}

}
