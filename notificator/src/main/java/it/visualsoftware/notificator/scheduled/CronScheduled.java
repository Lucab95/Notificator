package it.visualsoftware.notificator.scheduled;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import it.visualsoftware.notificator.dao.NotificationDao;
import it.visualsoftware.notificator.models.Notification;
import it.visualsoftware.notificator.redis.RedisMessagePublisher;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableAsync
@Slf4j
public class CronScheduled {
	private NotificationDao repository;
	@Autowired
	RedisMessagePublisher redis;
	@Value("${expire.interval}") 
	long interval;
	
	
	public CronScheduled(NotificationDao repository/*, RedisMessagePublisher redis*/) {
		this.repository=repository;
		//this.redis=redis;
	}
	
	@Async
	@Scheduled(cron ="${cron.string}")
	public List<Notification> next5() throws InterruptedException, JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer();
		log.info("stampa  alle "+ new Date());
		List<Notification> endSoon  = repository.nextMinutes(interval);
		log.info("get \n "+ endSoon.toString());
		for(Notification expiring : endSoon) {
			log.info("expiring"+expiring); 	
			String string =ow.writeValueAsString(expiring);
			//log.info("string \n"+string);
			//Notification x = objectMapper.readValue(string, Notification.class); 
			//redis.publish(string,new ChannelTopic("broadcast"));
			redis.publish(expiring);
		}
		return endSoon;
	}
	
}
