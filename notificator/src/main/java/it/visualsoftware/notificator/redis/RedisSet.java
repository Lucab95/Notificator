package it.visualsoftware.notificator.redis;

import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.visualsoftware.notificator.models.Notification;

public class RedisSet {
	protected final RedisTemplate<String,Object> redis;
	protected final ObjectMapper mapper;
	private final String setName;
	
	public RedisSet(RedisTemplate<String,Object> redis, ObjectMapper mapper ) {
		this.redis=redis;
		this.mapper=mapper;
		
	}
	public void add(Notification notify) {
		
		redis.opsForSet().add(setName, notify);
		
	}
}
