package it.visualsoftware.notificator.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.visualsoftware.notificator.dao.NotificationDao;
import it.visualsoftware.notificator.models.Notification;
import it.visualsoftware.notificator.redis.RedisHash;
import it.visualsoftware.notificator.redis.RedisQueueEx;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;

@Component
@EnableAsync
@Slf4j
public class TaskScheduled {
	private NotificationDao repository;
	//@Autowired
	//RedisMessagePublisher redis;
	private RedisQueueEx redisQueue;
	RedisHash hash;
	@Value("${expire.interval}") 
	long interval;
	
	
	
	
	
	
	public TaskScheduled(NotificationDao repository, RedisHash hash, RedisQueueEx redisQueue/*, RedisMessagePublisher redis*/) {
		this.repository=repository;
		this.hash= hash;
		this.redisQueue=redisQueue;
		//this.redis=redis;
	}
	/**
	 * schedula la prossima ora,  ogni ora dalle 7:50 alle 18:50
	 * @return
	 * @throws InterruptedException
	 * @throws JsonProcessingException
	 */
	@Async
	@Scheduled(cron ="${cron.string.hour}")
	@SchedulerLock(name = "TaskScheduler_nextHour", lockAtLeastForString = "PT5S", lockAtMostForString = "PT40S")
	public List<Notification> nextHour() throws InterruptedException, JsonProcessingException {
		int currentMin = 0;
		int hour = Calendar.getInstance().get(Calendar.HOUR)+1;
		String hashName = (hour%2==0) ?  "pari" : "pari";
		hash.flush(hashName);
		log.info("\n stampa  alle {} \n", Calendar.getInstance() );
		
		List<Notification> endSoon = repository.nextHour(interval);
		log.info("get {}", endSoon.size());
		
		List<Notification> inThisMin = new ArrayList<Notification>();
		for(Notification notify : endSoon) {//non esegue l'ultimo
			int min = notify.getMin();
			log.info("min {} e list {} ", currentMin, inThisMin);//errore
			if(min!=currentMin){
				//minuto diverso, invio la lista e la svuoto per il minuto successivo
				hash.put(hashName,currentMin,inThisMin);
				inThisMin.clear();
				currentMin=min;
			}
			inThisMin.add(notify);
		}
		hash.put(hashName,currentMin, inThisMin);
		
//		hash.get("expiring");
		return endSoon;
	}
	
	/**
	 * ogni minuto invia le notifiche per il blocco successivo
	 */
	@Async
	@Scheduled(cron="${cron.string.10min}")
	@Scheduled(cron ="${cron.string.min}")
	@SchedulerLock(name = "TaskScheduler_nextMin", lockAtLeastForString = "PT15S", lockAtMostForString = "PT40S")
	public void nextMin() throws InterruptedException, JsonProcessingException {
		Calendar now = Calendar.getInstance();
		log.info(" stampa  alle {} ", new Date() );
		now.add(Calendar.MINUTE, 10);
		int hour = now.get(Calendar.HOUR);
		int min = now.get(Calendar.MINUTE);
		log.info("print {}:{}",hour,min);
		String hashName = (hour%2==0) ?  "pari" : "pari";
		List<Notification> inThisMin = hash.get(hashName, min);
		log.info("Lista :"+inThisMin.toString());
		for (Notification notify : inThisMin )
			redisQueue.push(notify);
		
		//return endSoon;
	}
}