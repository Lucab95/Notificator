package it.visualsoftware.notificator.redis;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import it.visualsoftware.notificator.models.Notification;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisMessageListener implements MessageListener {
	public static Queue<Notification> messageQueue = new LinkedList<Notification>();
	//RQueue newQueue;
	public ObjectMapper mapper;
	//public static Jedis jedisQueue = new Jedis(
										//public final RQueue newQueue;
	public RedisMessageListener(RedisTemplate <String,Object> redis,ObjectMapper mapper) {
		this.mapper = mapper;
		//this.newQueue = new RQueue(redis, "queue");
		
	}




	@Override
    public void onMessage(Message message, byte[] pattern) {
		//Date date = new Date();
		log.info(" \n messaggio serializzato (onMessage) : {} ",message.toString());
		//newQueue.listener();
			try {
				//mapper.registerModule(new JavaTimeModule());
				log.info("dateformate {}",mapper.getDateFormat());
				Notification notify = mapper.readValue(message.getBody(), Notification.class);
				//JsonNode root = mapper.readTree(newState);
				//newQueue.push(notify);
				messageQueue.add(notify);
				//newQueue.push(notify);
				log.info("dimensione coda: {}  ", messageQueue.size());
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	//TODO OBJECT MAPPER INJECT
		
		//("queue",message.getBody()); // message.toString());
		//log.info("size is:"+messageQueue.size());
		//queue.lpush("message", date.toString());
    }

	
}