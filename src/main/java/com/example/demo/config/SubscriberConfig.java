package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.config.profile.SubscriberProfile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@SubscriberProfile
public class SubscriberConfig {

	@Bean
	public CommandLineRunner runner() {
		return (args) -> {
			log.info("Subscriber started");
		};
	}

}
