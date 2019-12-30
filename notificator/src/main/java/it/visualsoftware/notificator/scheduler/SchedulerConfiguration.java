package it.visualsoftware.notificator.scheduler;

import java.time.Duration;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.ScheduledLockConfiguration;
import net.javacrumbs.shedlock.spring.ScheduledLockConfigurationBuilder;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
@ComponentScan("it.visualsoftware.notificator.scheduler")

public class SchedulerConfiguration {
	//private final ENV = "default"
	
	@Bean
    public LockProvider lockProvider(DataSource dataSource) { //JedisPool jedisPool)  {
		Object jedisPool;
        return new JdbcTemplateLockProvider(dataSource, "shedlock");
    }
	
	 @Bean
	    public ScheduledLockConfiguration scheduledLockConfiguration(LockProvider lockProvider) {
	        return ScheduledLockConfigurationBuilder
	                .withLockProvider(lockProvider)
	                .withPoolSize(10)
	                .withDefaultLockAtMostFor(Duration.ofSeconds(30))
	                .build();
	    }
}
