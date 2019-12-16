package it.visualsoftware.notificator.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

//import lombok.extern.slf4j.Slf4j;
/**
 */
@Service
//@Slf4j
public class RedisMessageListener implements MessageListener {
	//public static Queue<Notification> messageQueue = new LinkedList<Notification>();
	//private final ObjectMapper mapper;
	//private RedisQueueEx redisQueue;
	public RedisMessageListener(ObjectMapper mapper, RedisQueueEx queue) {
//		this.mapper = mapper;
		//this.redisQueue=queue;
	}

	/**
	 * quando riceve un messaggio lo serializza e lo mette in coda
	 * @param message
	 * @param pattern
	 */
	@Override
    public void onMessage(Message message, byte[] pattern) {
//		
//		log.info(" \n\n messaggio serializzato (onMessage) : {} \n", message.toString() );
//			try {
//				Notification notify = mapper.readValue(message.getBody(), Notification.class);
//				//int min = notify.getEndDate().getMinute();
//				//log.info("object added");
//				//jedis.zadd
//				//redisQueue.push(notify);
//				//messageQueue.add(notify);
//				//log.info("dimensione coda: {}",messageQueue.size());
//			} catch (JsonParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (JsonMappingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
	//TODO OBJECT MAPPER INJECT
		
		//("queue",message.getBody()); // message.toString());
		//log.info("size is:"+messageQueue.size());
		//queue.lpush("message", date.toString());
    }

	
}