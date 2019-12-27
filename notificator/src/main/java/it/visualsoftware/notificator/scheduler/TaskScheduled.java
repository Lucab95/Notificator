package it.visualsoftware.notificator.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.visualsoftware.notificator.dao.NotificationDao;
import it.visualsoftware.notificator.models.Notification;
import it.visualsoftware.notificator.redis.RedisHash;
import it.visualsoftware.notificator.redis.RedisQueueEx;
import it.visualsoftware.notificator.redis.RedisSet;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;

@Component
@EnableAsync
@Slf4j
public class TaskScheduled {
	private NotificationDao repository;
	//@Autowired
	//RedisMessagePublisher redis;
	@Autowired
	ObjectMapper mapper;
	private RedisQueueEx redisQueue;
	RedisHash hash;
	@Autowired
	RedisSet set;
	@Value("${expire.interval}") 
	long interval;
	
	
	
	
	
	public TaskScheduled(NotificationDao repository, RedisHash hash, RedisQueueEx redisQueue/*, RedisMessagePublisher redis*/) {
		this.repository=repository;
		this.hash= hash;
		this.redisQueue=redisQueue;
		//this.redis=redis;
	}
	
	/**
	 * schedula le notifiche della prossima ora,  ogni ora dalle 7:50 alle 18:50
	 * @return
	 * @throws InterruptedException
	 * @throws JsonProcessingException
	 */
	@Async
	@Scheduled(cron ="${cron.string.hour}")
	@SchedulerLock(name = "TaskScheduler_nextHour", lockAtLeastForString = "PT5S", lockAtMostForString = "PT40S")
	public List<Notification> nextHour() throws InterruptedException, JsonProcessingException {
		int hour = Calendar.getInstance().get(Calendar.HOUR)+1;
		String hashName="pari";
		hash.flush(hashName);
		log.info("\n stampa  alle {} \n", Calendar.getInstance() );
		
		List<Notification> endSoon = repository.nextHour(interval);
		log.info("get {}", endSoon.size());
//		List<Notification> inThisMin = new ArrayList<Notification>();
		for(Notification notify : endSoon) {//non esegue l'ultimo
			
			String min = String.valueOf(notify.getMin());
			log.info("min {} e list {} ", min, notify);//errore
			hash.add(hashName,min,notify);
			
		}
		return endSoon;
	}
	
	/**
	 * Si occupa di incodare le notifiche da inviare minuto per minuto
	 * opera anche sull'insieme delle modifiche e le inserisce nella programmazione futura 
	 * @throws InterruptedException
	 * @throws JsonProcessingException
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
		String hashName = "pari";
		List<Object> map = set.popAll();
		//valutazione per rimuovere/ togliere / modificare
		log.info(""+map);
		//dare precedenza
		if (!(map.isEmpty())) {
			List<Notification> changes = mapper.convertValue(map, new TypeReference<List<Notification>>(){});
			for (Notification notify : changes){
				String evMin= String.valueOf(notify.getEndDate().getMinute());
				hash.add(hashName, evMin, notify);
				log.info("notify {} , {}",notify);
			}
		}
		//possibile eseguirlo dopo
		List<Notification> inThisMin = hash.get(hashName, String.valueOf(min));
		log.info("Lista :"+inThisMin.toString());
		for (Notification notify : inThisMin )
			redisQueue.push(notify);
	}
}