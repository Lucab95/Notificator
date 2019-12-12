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

import it.visualsoftware.notificator.models.Notification;
import lombok.extern.slf4j.Slf4j;
/**
 */
@Service
@Slf4j
public class RedisMessageListener implements MessageListener {
	//public static Queue<Notification> messageQueue = new LinkedList<Notification>();
	private final ObjectMapper mapper;
	private RedisQueueEx redisQueue; 
	public RedisMessageListener(ObjectMapper mapper, RedisQueueEx queue) {
		this.mapper = mapper;
		this.redisQueue=queue;
	}

	/**
	 * quando riceve un messaggio lo serializza e lo mette in coda
	 * @param message
	 * @param pattern
	 */
	@Override
    public void onMessage(Message message, byte[] pattern) {
		
		log.info(" \n\n messaggio serializzato (onMessage) : {} \n", message.toString() );
			try {
				Notification notify = mapper.readValue(message.getBody(), Notification.class);
				redisQueue.push(notify);
				//messageQueue.add(notify);
				//log.info("dimensione coda: {}",messageQueue.size());
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