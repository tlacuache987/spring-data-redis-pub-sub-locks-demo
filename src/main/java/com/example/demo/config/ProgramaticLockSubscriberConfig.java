package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.example.demo.config.condition.ProgrammaticLockCondition;
import com.example.demo.config.profile.KeepTryingProfile;
import com.example.demo.config.profile.ProgrammaticLockProfile;
import com.example.demo.config.profile.TryOnceProfile;
import com.example.demo.subscriber.ProgramaticLockRedisCustomerSubscriber;
import com.xvhx.distributed.locks.Lock;

@Configuration
@ProgrammaticLockProfile
public class ProgramaticLockSubscriberConfig {

	@Configuration
	@TryOnceProfile
	static class TryOnceManualLockSubscriberConfig {

		@Bean
		@Conditional(ProgrammaticLockCondition.class)
		public RedisMessageListenerContainer redisContainer(JedisConnectionFactory jedisConnectionFactory,
				MessageListenerAdapter messageListenerAdapter, ChannelTopic customerChannel) {
			RedisMessageListenerContainer container = new RedisMessageListenerContainer();
			container.setConnectionFactory(jedisConnectionFactory);
			container.addMessageListener(messageListenerAdapter, customerChannel);
			return container;
		}

		@Bean
		@Conditional(ProgrammaticLockCondition.class)
		public MessageListenerAdapter messageListenerAdapter(MessageListener manualLockRedisCustomerSubscriber) {
			return new MessageListenerAdapter(manualLockRedisCustomerSubscriber);
		}

		@Bean
		@Conditional(ProgrammaticLockCondition.class)
		public MessageListener manualLockRedisCustomerSubscriber(RedisTemplate<String, Object> redisTemplate,
				Lock simpleRedisLock) {
			ProgramaticLockRedisCustomerSubscriber manualLockRedisCustomerSubscriber = new ProgramaticLockRedisCustomerSubscriber(
					redisTemplate, simpleRedisLock);

			manualLockRedisCustomerSubscriber.setKeepTryingAcquireLock(false);

			return manualLockRedisCustomerSubscriber;
		}

	}

	@Configuration
	@KeepTryingProfile
	static class KeepTryingManualLockSubscriberConfig {

		@Bean
		@Conditional(ProgrammaticLockCondition.class)
		public RedisMessageListenerContainer redisContainer(JedisConnectionFactory jedisConnectionFactory,
				MessageListenerAdapter messageListenerAdapter, ChannelTopic customerChannel) {
			RedisMessageListenerContainer container = new RedisMessageListenerContainer();
			container.setConnectionFactory(jedisConnectionFactory);
			container.addMessageListener(messageListenerAdapter, customerChannel);
			return container;
		}

		@Bean
		@Conditional(ProgrammaticLockCondition.class)
		public MessageListenerAdapter messageListener(MessageListener manualLockRedisCustomerSubscriber) {
			return new MessageListenerAdapter(manualLockRedisCustomerSubscriber);
		}

		@Bean
		@Conditional(ProgrammaticLockCondition.class)
		public MessageListener manualLockRedisCustomerSubscriber(RedisTemplate<String, Object> redisTemplate,
				Lock simpleRedisLock) {
			ProgramaticLockRedisCustomerSubscriber manualLockRedisCustomerSubscriber = new ProgramaticLockRedisCustomerSubscriber(
					redisTemplate, simpleRedisLock);

			manualLockRedisCustomerSubscriber.setKeepTryingAcquireLock(true);

			return manualLockRedisCustomerSubscriber;
		}

	}

}
