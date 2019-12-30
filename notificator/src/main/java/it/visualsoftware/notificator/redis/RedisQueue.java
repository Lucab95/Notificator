package it.visualsoftware.notificator.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import lombok.extern.slf4j.Slf4j;
/**
 * Classe astratta che rappresenta la coda su Redis
 * @author andrea
 *
 * @param <T>
 */
@Slf4j
public abstract class RedisQueue<T> {

	protected final RedisTemplate<String,Object> redis;
	protected final String queueName;
	
	/**
	 * Costruttore
	 * @param redis
	 * @param executions
	 * @param queue
	 */
	public RedisQueue (RedisTemplate<String,Object> redis, String queue) {
		this.redis=redis;
		this.queueName=queue;
	}
	
	/**
	 * Push del Job sulla coda
	 * @param job
	 */
	public void push(T job) {
		log.info("Push Job to queue: {}",job);
		BoundListOperations<String, Object> operations=redis.boundListOps(queueName);
		operations.leftPush(job);
	}
	
	
	/**
	 * Listener sulla coda dei jobs
	 * @param executor
	 */
	@Async
	@SuppressWarnings("unchecked")
	public void listener() {
		log.info("Open listener on queue: {}",queueName);
		ListOperations<String, Object> operations=redis.opsForList();//rightPopAndLeftPush(queueName, "", timeout, unit)boundListOps();
		while (true) {
			T job = (T) operations.rightPopAndLeftPush(queueName, "executingJob",0,TimeUnit.SECONDS);
			if (job!=null) {
				log.info("Received job: {}", job);
				//Execution execution=executions.create(job.getTenantId(),ExecutionType.CAMPAIGN, job.getId(), job.getName());
				//Execution execution=executions.findOne(job.getTenantId(), job.getExecutionId());
				log.info("Executing job: {}", job);
				try {	
					//executor.exec(job.getTenantId(),job.getId(), execution.getId());
				} catch(Exception exc) {
					log.error("Error on executor: ",exc);
				}
				log.info("Job Executed: {}", job);
			}
		}
		
	}
	
}
