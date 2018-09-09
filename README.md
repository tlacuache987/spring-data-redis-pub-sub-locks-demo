====================================================================

Execute: mvn spring-boot:run to see How to run this POC.

=============

How to run this POC

1. Ensure Redis is up and running on localhost:6379, if Redis is running on different <host>:<port>, configure spring.redis.url property in application.properties file.

2. Open terminal and execute a publisher:
mvn spring-boot:run -Dspring.profiles.active=publisher
Publisher will start sending (publish) messages to subscribers every 5 seconds.

3. Open 'n' terminals and execute some subscribers, several subscribers can be executed as follows (in different terminals):
mvn spring-boot:run -Dspring.profiles.active=subscriber,annotation-lock,keep-trying
or
mvn spring-boot:run -Dspring.profiles.active=subscriber,annotation-lock,try-once
or
mvn spring-boot:run -Dspring.profiles.active=subscriber,programmatic-lock,keep-trying
or
mvn spring-boot:run -Dspring.profiles.active=subscriber,programmatic-lock,try-once

4. Analyze configuration to understand what's under the hood.

5. Profiles:
* annotation-lock and keep-trying profiles start subscriber and locks message listener using Annotations with Spring AOP aspects, and every subscriber will be trying keep the lock until its available, subscribers starts race conndition.

* annotation-lock and try-once profiles start subscriber and locks message listener using Annotations with Spring AOP aspects, and every subscriber will start a race condition to keep the lock but just one subscriber will be the leader, each others sebscribers will avoid waiting for the lock as any other subscriber won the lock.

* programmatic-lock and keep-trying profiles start subscriber and locks message listener programmatically using fluent API, and every subscriber will be trying keep the lock until its available, subscribers starts race conndition.

* programmatic-lock and try-once profiles start subscriber and locks message listener programmatically using fluent API, and every subscriber will start a race condition to keep the lock but just one subscriber will be the leader, each others sebscribers will avoid waiting for the lock as any other subscriber won the lock.

====================================================================
