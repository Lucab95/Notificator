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
//  private RedisHash hash;
//	private final String hashName;
	private RedisSet set;
	
	public RedisMessageListenerEvictor(ObjectMapper mapper, RedisSet set) {//, RedisHash hash) {
		this.mapper=mapper;
		this.set=set;
		//this.hash=hash;
//		this.hashName = "pari";
	}
	
	//TODO AGGIUNGERE controllo per quando lo scheduler parte dopo le 7 e 50 
	@Override
	public void onMessage(Message message, byte[] pattern) {
		//ora di adesso, se entro prossima ora -> modifica altrimenti nulla
		try {
			Notification notify = mapper.readValue(message.getBody(), Notification.class);
			LocalDateTime now= LocalDateTime.now().plusMinutes(10);
			LocalDateTime eventTime = notify.getEndDate();
			log.info("ora di adesso {} e dell'evento {} \n", now, eventTime);
			if ((eventTime.getHour()==now.getHour())&&(eventTime.getMinute()>now.getMinute())){
				set.add(notify);

			}
			else {
				log.info("insert 0");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
