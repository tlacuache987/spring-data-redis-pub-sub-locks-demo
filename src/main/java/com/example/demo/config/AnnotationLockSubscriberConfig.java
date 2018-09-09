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

import com.example.demo.config.condition.AnnotationLockCondition;
import com.example.demo.config.profile.AnnotationLockProfile;
import com.example.demo.config.profile.KeepTryingProfile;
import com.example.demo.config.profile.TryOnceProfile;
import com.example.demo.service.locked.LockedTask;
import com.example.demo.service.locked.impl.KeepTryingLockedTask;
import com.example.demo.service.locked.impl.TryOnceLockedTask;
import com.example.demo.subscriber.AnnotationLockRedisCustomerSubscriber;

@Configuration
@AnnotationLockProfile
public class AnnotationLockSubscriberConfig {

	@Configuration
	@TryOnceProfile
	static class TryOnceAnnotationLockSubscriberConfig {

		@Bean
		@Conditional(AnnotationLockCondition.class)
		public RedisMessageListenerContainer redisContainer(JedisConnectionFactory jedisConnectionFactory,
				MessageListenerAdapter annotationListenerAdapter, ChannelTopic customerChannel) {
			RedisMessageListenerContainer container = new RedisMessageListenerContainer();
			container.setConnectionFactory(jedisConnectionFactory);
			container.addMessageListener(annotationListenerAdapter, customerChannel);
			return container;
		}

		@Bean
		@Conditional(AnnotationLockCondition.class)
		public MessageListenerAdapter annotationListenerAdapter(MessageListener annotationLockRedisCustomerSubscriber) {
			return new MessageListenerAdapter(annotationLockRedisCustomerSubscriber);
		}

		@Bean
		@Conditional(AnnotationLockCondition.class)
		public MessageListener annotationLockRedisCustomerSubscriber(LockedTask tryOnceLockedTask) {
			return new AnnotationLockRedisCustomerSubscriber(tryOnceLockedTask);
		}

		@Bean
		@Conditional(AnnotationLockCondition.class)
		public LockedTask tryOnceLockedTask(RedisTemplate<String, Object> redisTemplate) {
			return new TryOnceLockedTask(redisTemplate);
		}

	}

	@Configuration
	@KeepTryingProfile
	static class KeepTryingAnnotationLockSubscriberConfig {

		@Bean
		@Conditional(AnnotationLockCondition.class)
		public RedisMessageListenerContainer redisContainer(JedisConnectionFactory jedisConnectionFactory,
				MessageListenerAdapter annotationListenerAdapter, ChannelTopic customerChannel) {
			RedisMessageListenerContainer container = new RedisMessageListenerContainer();
			container.setConnectionFactory(jedisConnectionFactory);
			container.addMessageListener(annotationListenerAdapter, customerChannel);
			return container;
		}

		@Bean
		@Conditional(AnnotationLockCondition.class)
		public MessageListenerAdapter annotationListenerAdapter(MessageListener annotationLockRedisCustomerSubscriber) {
			return new MessageListenerAdapter(annotationLockRedisCustomerSubscriber);
		}

		@Bean
		@Conditional(AnnotationLockCondition.class)
		public MessageListener annotationLockRedisCustomerSubscriber(LockedTask keepTryingLockedTask) {
			return new AnnotationLockRedisCustomerSubscriber(keepTryingLockedTask);
		}

		@Bean
		@Conditional(AnnotationLockCondition.class)
		public LockedTask keepTryingLockedTask(RedisTemplate<String, Object> redisTemplate) {
			return new KeepTryingLockedTask(redisTemplate);
		}

	}

}
