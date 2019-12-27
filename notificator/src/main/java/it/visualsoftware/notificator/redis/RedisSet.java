package it.visualsoftware.notificator.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.visualsoftware.notificator.models.Notification;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

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
		long x =redis.opsForSet().add(setName, notify);
		log.info("insert {}",x);
	}
	
	//TODO vedere transazione per la pop/size
	/**
	 * rimuove tutti gli elementi dall'insieme e li ritorna in output
	 * @return List<Object>
	 */
	@Transactional
	public List<Object> popAll(){
		long size = redis.opsForSet().size(setName);
		log.info("size: {}", size);
		redis.opsForSet().pop(setName, size);
		List<Object> objectList = redis.opsForSet().pop(setName, size);
		if (objectList.isEmpty()) {
			return new ArrayList<Object>();
		}else {
			return objectList;
		}
		
		
	}
	
	//useless
	public void remove(Notification notify) {
		log.info(""+redis.opsForSet().remove(setName,notify));	
		}
	
	//dim dell'insieme
	public Long size() {
		return redis.opsForSet().size(setName);
	}
	
}
