package it.visualsoftware.notificator.redis;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;

import com.fasterxml.jackson.databind.ObjectMapper;

import Exception.NotificationSentException;
import it.visualsoftware.notificator.RestTemplate.RestTemplateService;
import it.visualsoftware.notificator.models.Notification;
import lombok.extern.slf4j.Slf4j;



//********INVIA NOTIFICHE********
@Slf4j
public class RedisQueueEx extends RedisQueue<Notification>{

	public RedisQueueEx(RedisTemplate<String,Object> redis,String queue) {
		super(redis, queue);
	}
	
	/**
	 * Aggiunge una notifica alla coda di invio
	 */
	@Override
	public void push(Notification job) {
		log.info("Push Job to queue: {}",job);
		BoundListOperations<String, Object> operations=redis.boundListOps(queueName);
		operations.leftPush(job);
	}
	
	/**
	 * listener per eseguire le richieste 
	 * @param mapper
	 * @param template
	 */
	@Async
	public void listener(ObjectMapper mapper, RestTemplateService template) {
		log.info("Open listener on queue: {}",queueName);
		ListOperations<String, Object> operations=redis.opsForList();
		while (true) {
			Object job = (Object) operations.rightPop(queueName, 0, TimeUnit.SECONDS);
			if (job!=null) {
				log.info("Executing job: {}", mapper.convertValue(job, Notification.class));
				try {
					log.info("exec");
					 ResponseEntity<String> response = template.SendNotification(mapper.convertValue(job, Notification.class));
					 if(response.getStatusCode().is2xxSuccessful()) {
							log.info("response {}", response.getBody());
						}
					 else {
						 throw new NotificationSentException(response.getStatusCodeValue());
						//caso Errore (1xx,3xx,4xx,5xx), setto l'errore e salvo 
					 }
				} catch(Exception exc) {
					log.error("Error on executor: ",exc);
				}
				log.info("Job Executed: {}", job);
			}
		}
	}
}
