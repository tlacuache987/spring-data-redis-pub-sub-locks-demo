package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.example.demo.config.profile.HowToProfile;
import com.xvhx.distributed.locks.redis.autoconfiguration.EnableDistributedLocksWithRedis;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableDistributedLocksWithRedis
@EnableAspectJAutoProxy
public class SpringDataRedisPubSubLocksDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataRedisPubSubLocksDemoApplication.class, args);
	}

	@Bean
	@HowToProfile
	public CommandLineRunner howTo() {

		return (args) -> {
			log.info("====================================================================");
			log.info("How to run this POC");
			log.info("");
			log.info(
					"1. Ensure Redis is up and running on localhost:6379, if Redis is running on different <host>:<port>, configure spring.redis.url property in application.properties file.");
			log.info("");
			log.info("2. Open terminal and execute a publisher:");
			log.info("\tmvn spring-boot:run -Dspring.profiles.active=publisher");
			log.info("\tPublisher will start sending (publish) messages to subscribers every 5 seconds.");
			log.info("");
			log.info(
					"3. Open 'n' terminals and execute some subscribers, several subscribers can be executed as follows (in different terminals):");
			log.info("\tmvn spring-boot:run -Dspring.profiles.active=subscriber,annotation-lock,keep-trying");
			log.info("\tor");
			log.info("\tmvn spring-boot:run -Dspring.profiles.active=subscriber,annotation-lock,try-once");
			log.info("\tor");
			log.info("\tmvn spring-boot:run -Dspring.profiles.active=subscriber,programmatic-lock,keep-trying");
			log.info("\tor");
			log.info("\tmvn spring-boot:run -Dspring.profiles.active=subscriber,programmatic-lock,try-once");
			log.info("");
			log.info("4. Analyze configuration to understand what's under the hood.");
			log.info("");
			log.info("5. Profiles:");
			log.info(
					"\t* annotation-lock and keep-trying profiles start subscriber and locks message listener using Annotations with Spring AOP aspects, and");
			log.info(
					"\t  every subscriber will be trying keep the lock until its available, subscribers starts race conndition.");
			log.info("");
			log.info(
					"\t* annotation-lock and try-once profiles start subscriber and locks message listener using Annotations with Spring AOP aspects, and");
			log.info(
					"\t  every subscriber will start a race condition to keep the lock but just one subscriber will be the leader, each others sebscribers");
			log.info("\t  will avoid waiting for the lock as any other subscriber won the lock.");
			log.info("");
			log.info(
					"\t* programmatic-lock and keep-trying profiles start subscriber and locks message listener programmatically using fluent API, and");
			log.info(
					"\t  every subscriber will be trying keep the lock until its available, subscribers starts race conndition.");
			log.info("");
			log.info(
					"\t* programmatic-lock and try-once profiles start subscriber and locks message listener programmatically using fluent API, and");
			log.info(
					"\t  every subscriber will start a race condition to keep the lock but just one subscriber will be the leader, each others sebscribers");
			log.info("\t  will avoid waiting for the lock as any other subscriber won the lock.");

			log.info("====================================================================");
		};
	}

}
