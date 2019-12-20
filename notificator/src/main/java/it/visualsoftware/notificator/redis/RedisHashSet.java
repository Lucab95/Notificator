package it.visualsoftware.notificator.redis;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
//import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.visualsoftware.notificator.RestTemplate.RestTemplateService;
import it.visualsoftware.notificator.models.Notification;
import lombok.extern.slf4j.Slf4j;

//********STRUTTURA HASH PER GESTIONE NOTIFICHE********
@Slf4j
public class RedisHashSet {
	protected final RedisTemplate<String,Object> redis;
	protected final ObjectMapper mapper; 
	
	
	public RedisHashSet(RedisTemplate<String,Object> redis, ObjectMapper mapper ) {
		this.redis=redis;
		this.mapper=mapper;
	}
	
	public void put(String hashName, int key , List<Notification> inThisMin) {
		log.info("add scheduled notification");
		redis.opsForHash().put(hashName, String.valueOf(key), inThisMin);
//		log.info("redis" + redis.opsForHash().get(hashName, "dog"));
		}
	
	public List<Notification> get(String hashName, int key) {
		log.info("try get");
		//BoundHashOperations<String, String, Notification> operations = redis.boundHashOps(hashName);
		Object map = redis.opsForHash().get(hashName,String.valueOf(key));
		if (map==null) {
			log.info("no keys found");
			return new ArrayList<Notification>();
		}
		List<Notification> minuteList = mapper.convertValue(map, new TypeReference<List<Notification>>(){});
		return minuteList;
	}
	
	public void flush(String hashName) {
		log.info("deleted"+hashName);
		redis.delete(hashName);
	}

	public void putSet(String hashName, int min, RedisSet set) {
		List<Notification> x = get(hashName,min);
		Set<Object> y = set.members();
		Iterator<Object> value = y.iterator();
		while(value.hasNext()) {
			Notification obj = mapper.convertValue(value.next(), Notification.class);
			if (!(x.contains(obj))){
			x.add(obj);
		}
		}
		put(hashName,min,x);
	}
}
