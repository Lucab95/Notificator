package it.visualsoftware.notificator.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
	
//	/**
//	 * Restituisce la lista dei job in running per il tenant specificato
//	 * @return
//	 */
//	public List<N> getAllRunningJobs(){
//		BoundListOperations<String, Object> operations=redis.boundListOps("executingJob");
//		var list=operations.range(0, operations.size());
//		List<Job> allJobs=(List<Job>)(Object)list;
//		return allJobs;
//	}
//	
//	/**
//	 * Restituisce la lista dei job in running per il tenant specificato
//	 * @return
//	 */
//	public List<Job> getRunningJobs(String tenantId){
//		BoundListOperations<String, Object> operations=redis.boundListOps("executingJob");
//		var list=operations.range(0, operations.size());
//		
//		List<Job> allJobs=(List<Job>)(Object)list;
//		List<Job> tenantJobs=new ArrayList<>();
//		for (Job j:allJobs) {
//			if (j.getTenantId().equals(tenantId))
//				tenantJobs.add(j);
//		}
//		return tenantJobs;
//	}
//	
//	/**
//	 * Restituisce la lista delle campagne  in running
//	 * @return
//	 */
//	public Set<String> getRunningCampaigns(String tenantId){
//		Set<String> campaigns=new TreeSet<>();
//		List<Job> jobs=getRunningJobs(tenantId);
//		for(Job j:jobs) {
//			campaigns.add(j.getCampaignId());
//		}
//		return campaigns;
//	}
//	
//	/**
//	 * Restituisce la lista delle campagne  in running
//	 * @return
//	 */
//	public Set<String> getAllRunningCampaigns(){
//		Set<String> campaigns=new TreeSet<>();
//		List<Job> jobs=getAllRunningJobs();
//		for(Job j:jobs) {
//			campaigns.add(j.getCampaignId());
//		}
//		return campaigns;
//	}
//	
//	/**
//	 * Verifica se il job è già in modalità running
//	 * @param job
//	 * @return
//	 */
//	public boolean isRunning(Job job) {
//		BoundListOperations<String, Object> operations=redis.boundListOps("executingJob");
//		List<?> list=operations.range(0, operations.size());
//		for (Object jobObject:list) {
//			Job j=(Job)jobObject;
//			if (j.getTenantId().equals(job.getTenantId()) && j.getId().equals(job.getId())) {
//				log.info("NOT executing another one {}",job);
//				return true;
//			}
//		}
//		return false;
//	}	
	
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
