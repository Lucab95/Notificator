package it.visualsoftware.notificator.redis;


import java.util.List;
//import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;

import it.visualsoftware.notificator.models.Notification;
import lombok.extern.slf4j.Slf4j;

//********STRUTTURA HASH PER GESTIONE NOTIFICHE********
@Slf4j
public class RedisHash {
	protected final RedisTemplate<String,Object> redis;
	protected final String hashName;
	
	public RedisHash(RedisTemplate<String,Object> redis,String hashName) {
		this.redis=redis;
		this.hashName=hashName;
	}
	
	public void put(int key , List<Notification> notify) {
		
		log.info("add scheduled notification");
		redis.opsForHash().put(hashName, String.valueOf(key), notify);
//		log.info("redis" + redis.opsForHash().get(hashName, "dog"));

		}
	
	public Object get(String key) {
		log.info("try get");
		//BoundHashOperations<String, String, Notification> operations = redis.boundHashOps(hashName);
		Object map = redis.opsForHash().get(hashName,key);
		log.info("asdad"+redis.opsForHash().keys("15"));
		if (map==null) {
			log.info("no keys found");
			return null;
		}
		/*redis.opsForHash().delete(key, *);*/
		log.info("keys found"+map.toString());
		//log.info("map " + map.size());
		return map;
	}
	
	public void flush() {
		redis.delete(hashName);
	}
}
