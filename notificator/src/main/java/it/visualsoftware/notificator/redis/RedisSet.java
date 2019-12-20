package it.visualsoftware.notificator.redis;

import java.util.Set;

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
	public void add(Notification notify) {
		long x =redis.opsForSet().add(setName, notify);
		log.info("insert {}",x);
	}
	
	public Set<Object> members() {
		return redis.opsForSet().members(setName);
	}
	public void remove(Notification notify) {
		log.info(""+redis.opsForSet().remove(setName,notify));	
		}
	
}
