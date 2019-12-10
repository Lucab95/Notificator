package it.visualsoftware.notificator.redis;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.visualsoftware.notificator.models.Notification;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisMessageListener implements MessageListener {
	public static Queue<Notification> messageQueue = new LinkedList<Notification>();
	//public static Jedis jedisQueue = new Jedis();
	public ObjectMapper objectMapper;
	public RedisMessageListener(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}




	@Override
    public void onMessage(Message message, byte[] pattern) {
		//Date date = new Date();
		log.info("Stampa \n "+message.toString());
		Notification notify;
			try {
				notify = objectMapper.readValue(message.getBody(), Notification.class);
				messageQueue.add(notify);
				log.info("add"+messageQueue.size());
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

			
		
		//Notification body = message.getBody();
	    //String str = new String(body);
		
	//TODO OBJECT MAPPER INJECT
		
		//("queue",message.getBody()); // message.toString());
		//log.info("size is:"+messageQueue.size());
		//queue.lpush("message", date.toString());
		
		
    }

	
}