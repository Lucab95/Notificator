package it.visualsoftware.notificator.redis;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.visualsoftware.notificator.models.Notification;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisMessageListenerEvictor implements MessageListener{
	private  final ObjectMapper mapper;
	private RedisHash hash;
//	private final String hashName;
	private RedisSet set;
	
	public RedisMessageListenerEvictor(ObjectMapper mapper, RedisSet set, RedisHash hash) {
		this.mapper=mapper;
		this.set=set;
		this.hash=hash;
//		this.hashName = "pari";
	}
	
	//TODO AGGIUNGERE controllo per quando lo scheduler parte dopo le 7 e 50 
	@Override
	public void onMessage(Message message, byte[] pattern) {
		//ora di adesso, se entro prossima ora -> modifica altrimenti nulla
		String hashName = "pari";
		try {
			
			Notification notify = mapper.readValue(message.getBody(), Notification.class);
			LocalDateTime now= LocalDateTime.now().plusMinutes(10);
			LocalDateTime eventTime = notify.getEndDate();
			log.info("\n ora di adesso {} e dell'evento {} \n", now, eventTime);
			
			if ((eventTime.getHour()==now.getHour())&&(eventTime.getMinute()>now.getMinute())){
//				log.info("da getire");
				int min = eventTime.getMinute();
				List<Notification> x = hash.get(hashName,min);
				x.add(notify);
				log.info("lista" + x);
				hash.put(hashName, min, x);

				set.add(notify);
				log.info("uff"+set.members());
				
//				log.info("contains:{}",inThisMin.toString());
				
				//put in set
//				if (inThisMin.contains(notify)) {
//					log.info("salta inserimento");
//				}else {
//					notify.setTitle(LocalDateTime.now().toString());
//					log.info("aggiunto {}",inThisMin.add(notify));
//					hash.put(hashName, min, inThisMin);
//					log.info("\n");
//					log.info("list "+ hash.get(hashName, min));
//					log.info("dopo inserimento contains:{}",inThisMin.contains(notify));
//				}
				
				
				
			}
//			else {
//				log.info("scorri");
//			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
