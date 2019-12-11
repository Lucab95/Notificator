package it.visualsoftware.notificator.redis;

import org.springframework.data.redis.core.RedisTemplate;

import it.visualsoftware.notificator.models.Notification;

public class RedisQueueEx extends RedisQueue<Notification>{

	public RedisQueueEx(RedisTemplate<String,Object> redis, String queue) {
		super(redis, queue);
		// TODO Auto-generated constructor stub
	}

}
