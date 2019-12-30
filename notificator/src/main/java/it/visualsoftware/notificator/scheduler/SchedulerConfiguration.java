package it.visualsoftware.notificator.scheduler;


import java.lang.reflect.Field;
import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import net.javacrumbs.shedlock.provider.redis.jedis.JedisLockProvider;
import net.javacrumbs.shedlock.spring.ScheduledLockConfiguration;
import net.javacrumbs.shedlock.spring.ScheduledLockConfigurationBuilder;
import redis.clients.jedis.Jedis;
//import redis.clients.jedis.util.Pool;
import redis.clients.util.Pool;

@Configuration
@Profile("scheduler")
public class SchedulerConfiguration {
	
	private static final String ENV = "LM";	
	
	@Bean
	public ScheduledLockConfiguration taskScheduler(LockProvider lockProvider) {
	    return ScheduledLockConfigurationBuilder
	        .withLockProvider(lockProvider)
	        .withPoolSize(10)
	        .withDefaultLockAtMostFor(Duration.ofMinutes(30))	        
	        .build();
	}	
	//TODO Parlare con andrea, per problemi dependency
	@SuppressWarnings("unchecked")
	@Bean
	public LockProvider lockProvider(JedisConnectionFactory factory) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field privatePool=JedisConnectionFactory.class.getDeclaredField("pool");
		privatePool.setAccessible(true);
		Pool<Jedis> pool=(Pool<Jedis>)privatePool.get(factory);
	    return new JedisLockProvider(pool, ENV);
	}
	
	@Bean 
	public LockingTaskExecutor lockingTaskExecutor(LockProvider lockProvider) {
		return new DefaultLockingTaskExecutor(lockProvider);
	}

}

