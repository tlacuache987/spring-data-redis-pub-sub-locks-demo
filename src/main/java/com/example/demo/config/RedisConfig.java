package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import com.example.demo.config.condition.NotHowToProfileActiveCondition;
import com.example.demo.config.profile.PublisherSubscriberProfile;

@Configuration
@PublisherSubscriberProfile
public class RedisConfig {

	@Bean
	@Conditional(NotHowToProfileActiveCondition.class)
	public JedisConnectionFactory jedisConnectionFactory() {
		return new JedisConnectionFactory();
	}

	@Bean
	@Conditional(NotHowToProfileActiveCondition.class)
	public RedisTemplate<String, Object> redisTemplate() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(jedisConnectionFactory());
		// template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
		template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		return template;
	}
}
