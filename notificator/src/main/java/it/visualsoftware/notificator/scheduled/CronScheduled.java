package it.visualsoftware.notificator.scheduled;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

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
	
	/**
	 * schedula la prossima ora,  ogni ora dalle 7:50 alle 18:50
	 * @return
	 * @throws InterruptedException
	 * @throws JsonProcessingException
	 */
	@Async
	@Scheduled(cron ="${cron.string.min}")
	public List<Notification> nextHour() throws InterruptedException, JsonProcessingException {
		log.info("\n stampa  alle {} \n", new Date() );
		List<Notification> endSoon = repository.nextHour(interval);
		//List<Notification> endSoon  = repository.nextMinutes(interval);
		log.info("get {}", endSoon.size());
		/*for(Notification expiring : endSoon) {
			log.info("expiring"+expiring);
			redis.publish(expiring);
		}*/
		return endSoon;
	}
	
	/**
	 * ogni minuto invia le notifiche per il blocco successivo
	 */
//	@Async
//	@Scheduled(cron ="${cron.string.min}")
//	public List<Notification> nextMin() throws InterruptedException, JsonProcessingException {
//		log.info("\n stampa  alle {} \n", new Date() );
//		List<Notification> endSoon  = repository.nextMinutes(interval);
//		log.info("get \n "+ endSoon.toString());
//		for(Notification expiring : endSoon) {
//			log.info("expiring"+expiring);
//			redis.publish(expiring);
//		}
//		return endSoon;
//	}
}
