package it.visualsoftware.notificator.redis;


import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;

import it.visualsoftware.notificator.models.Notification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisHash<T> {
	protected final RedisTemplate<String,Object> redis;
	protected final String hashName;
	
	public RedisHash(RedisTemplate<String,Object> redis,String hashName) {
		this.redis=redis;
		this.hashName=hashName;
	}
	
	public void put(String key , Notification notify) {
		
		log.info("add scheduled notification");
		redis.opsForHash().put(hashName, key, notify);;
		}
	
	public Map<Object,Object> get(String key) {
		log.info("try get");
		//BoundHashOperations<String, String, Notification> operations = redis.boundHashOps(hashName);
		Map<Object,Object> map = redis.opsForHash().entries(key);
		if (map==null) {
			log.info("no keys found");
			return null;
		}
		/*redis.opsForHash().delete(key, *);*/
		log.info("keys found");
		return map;
	}
}
