package com.Shubham.devconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DevconnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevconnectApplication.class, args);
	}

}
