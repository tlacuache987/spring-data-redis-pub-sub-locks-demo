package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

import com.example.demo.config.profile.PublisherSubscriberProfile;

@Configuration
@PublisherSubscriberProfile
public class RedisPubSubConfig {

	@Bean
	public ChannelTopic customerChannel() {
		return new ChannelTopic("customerChannel");
	}
}
