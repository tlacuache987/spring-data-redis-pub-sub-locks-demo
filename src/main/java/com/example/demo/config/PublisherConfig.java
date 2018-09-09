package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.example.demo.config.profile.PublisherProfile;
import com.example.demo.model.Address;
import com.example.demo.model.Customer;
import com.example.demo.publisher.CustomerPublisher;
import com.example.demo.publisher.impl.CustomerRedisPublisher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@PublisherProfile
public class PublisherConfig {

	@Bean
	public CommandLineRunner runner(CustomerPublisher customerRedisPublisher,
			ThreadPoolTaskScheduler threadPoolTaskScheduler) {

		return new CommandLineRunner() {

			Long messageCount = 0L;

			@Override
			public void run(String... args) throws Exception {
				log.info("Publisher started");

				threadPoolTaskScheduler.scheduleWithFixedDelay(new Runnable() {

					@Override
					public void run() {
						customerRedisPublisher.publish(
								new Customer(messageCount, "Ivan " + messageCount, "Garcia " + messageCount,
										new Address("Uxmal", "7", "Del Valle")));
						messageCount++;
					}
				}, 5000);

			}
		};
	}

	@Bean
	public CustomerPublisher customerRedisPublisher(RedisTemplate<String, Object> redisTemplate,
			ChannelTopic customerChannel) {
		return new CustomerRedisPublisher(redisTemplate, customerChannel);
	}

	@Bean
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(5);
		threadPoolTaskScheduler.setThreadNamePrefix(
				"pub-scheduler");
		return threadPoolTaskScheduler;
	}
}
