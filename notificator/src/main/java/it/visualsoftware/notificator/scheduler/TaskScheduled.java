package it.visualsoftware.notificator.scheduler;

import java.util.ArrayList;
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
import it.visualsoftware.notificator.redis.RedisHash;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableAsync
//@EnableSchedulerLock(defaultLockAtMostFor = "PT45S")
@Slf4j
public class TaskScheduled {
	private NotificationDao repository;
	//@Autowired
	//RedisMessagePublisher redis;
	@Autowired
	RedisHash hash;
	@Value("${expire.interval}") 
	long interval;
	
	
	public TaskScheduled(NotificationDao repository/*, RedisMessagePublisher redis*/) {
		this.repository=repository;
		//this.redis=redis;
	}
	
	/**
	 * schedula la prossima ora,  ogni ora dalle 7:50 alle 18:50
	 * @return
	 * @throws InterruptedException
	 * @throws JsonProcessingException
	 */
	//TODO INSERIRE L'ULTIMO ELEMENTO, E PREVEDERE COSA SUCCEDE QUANDO MANCANO 10 MINUTI E QUINDI ANDREBBERO PERSI
	@Async
	@Scheduled(cron ="${cron.string.min}")
	/*@SchedulerLock(name = "TaskScheduler_nextHour", 
    lockAtLeastForString = "PT5S", lockAtMostForString = "PT40S")*/
	public List<Notification> nextHour() throws InterruptedException, JsonProcessingException {
		hash.flush();
		log.info("\n stampa  alle {} \n", new Date() );
		List<Notification> endSoon = repository.nextHour(interval);
		//List<Notification> endSoon  = repository.nextMinutes(interval);
		//hash.put("expiring", endSoon);
		log.info("get {}", endSoon.size());
		int currentMin = 0;
		List<Notification> inThisMin = new ArrayList<Notification>();
		for(Notification notify : endSoon) {//non esegue l'ultimo
			int min = notify.getMin();
			if (currentMin==0) 
				currentMin=min;
			log.info("min {} e list {} ", currentMin, inThisMin);
			if(min!=currentMin){
				//minuto diverso, invio la lista e la svuoto per il minuto successivo
				hash.put(currentMin,inThisMin);
				inThisMin.clear();
				currentMin=min;
			}
			inThisMin.add(notify);
		}
		//hash.put(currentMin, inThisMin);
		
//		hash.get("expiring");
		return endSoon;
	}
//	
//	/**
//	 * ogni minuto invia le notifiche per il blocco successivo
//	 */
//	@Async
//	@Scheduled(cron ="${cron.string.min}")
//	@SchedulerLock(name = "TaskScheduler_nextHour", 
//    lockAtLeastForString = "PT15S", lockAtMostForString = "PT40S")
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