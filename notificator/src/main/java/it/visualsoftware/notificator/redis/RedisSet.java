package it.visualsoftware.notificator.redis;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.visualsoftware.notificator.models.Notification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisSet {
	protected final RedisTemplate<String,Object> redis;
	protected final ObjectMapper mapper;
	private final String setName;

	
	public RedisSet(RedisTemplate<String,Object> redis, ObjectMapper mapper, String setName) {
		this.redis=redis;
		this.mapper=mapper;
		this.setName=setName;
	}
	/**
	 *aggiunge un elemento all'insieme
	 * @param notify
	 */
	public void add(Notification notify) {
		long inserted =redis.opsForSet().add(setName, notify);
		if (inserted==1) {
			log.info("inserted ");
		}else {
			log.info("failed ");
		}
		
	}
	
	//TODO ricontrollare con andrea e vedere se va ciclata in casa di fallimento
	/**
	 * rimuove tutti gli elementi dall'insieme e li ritorna in output
	 * @return List<Object>
	 */
	public List<Object> popAll(){
		redis.watch(setName);
		long size = redis.opsForSet().size(setName);
		redis.multi();
		log.info(""+size);
		redis.opsForSet().pop(setName,size);
		List<Object> objectList =  redis.exec();
//		log.info( ""+redis.exec());
//		List<Object> objectList =  new ArrayList<Object>();
		redis.unwatch();
		if (objectList.isEmpty()) {
			return new ArrayList<Object>();
		}else {
			return objectList;
		}	
	}
	
}
