package com.example.demo.config.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ProgrammaticLockCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		return context.getEnvironment().acceptsProfiles("programmatic-lock")
				&& !context.getEnvironment().acceptsProfiles("annotation-lock");
	}

}
