package it.visualsoftware.notificator.redis;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.visualsoftware.notificator.RestTemplate.RestTemplateService;
import it.visualsoftware.notificator.models.Notification;
import lombok.extern.slf4j.Slf4j;



//********INVIA NOTIFICHE********
@Slf4j
public class RedisQueueEvents extends RedisQueue<Notification>{
	@Autowired
	RedisHash hash;
	
	
	public RedisQueueEvents(RedisTemplate<String,Object> redis,String queue) {
		super(redis, queue);
	}
	
	@Override
	public void push(Notification job) {
		log.info("Push Job to queue: {}",job);
		BoundListOperations<String, Object> operations=redis.boundListOps(queueName);
		operations.leftPush(job);
	}
	/**
	 * listener per eseguire gli eventi
	 * @param mapper
	 * @param template
	 */
	@Async
	public void listener(ObjectMapper mapper, RestTemplateService template) {
		log.info("Open listener on queue: {}",queueName);
		ListOperations<String, Object> operations=redis.opsForList();//rightPopAndLeftPush(queueName, "", timeout, unit)boundListOps();
		while (true) {
			Object job = (Object) operations.rightPop(queueName, 0, TimeUnit.SECONDS);
			//T job = (T) operations.rightPop(queueName);
			if (job!=null) {
				log.info("Received Notification: {}", job);
				LocalDateTime now= LocalDateTime.now().plusMinutes(10);
				Notification notify = mapper.convertValue(job, Notification.class);
				LocalDateTime eventTime = notify.getEndDate();
				log.info("\n ora di adesso {} e dell'evento {} \n", now, eventTime);
				if ((eventTime.getHour()==now.getHour())&&(eventTime.getMinute()>now.getMinute())){
					int min = eventTime.getMinute();
						//transazione
						List<Notification> inThisMin = hash.get("pari",min);
						inThisMin.add(notify);
						hash.put("pari", min, inThisMin);
						//end
					//x.add(notify);
//					switch (notify.getTenant()){
//					case "lol":
//						return "x" ;
//					case "modify":
//						return "lol";
//				}
					
				}
			}
		}
	}
}
