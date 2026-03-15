package com.Shubham.devconnect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@EnableJpaAuditing
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.Shubham.devconnect.repository")
public class DevconnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(DevconnectApplication.class, args);
	}

}
