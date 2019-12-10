package it.visualsoftware.notificator.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import it.visualsoftware.notificator.models.Notification;
import lombok.extern.slf4j.Slf4j;

/**
 * Servizio per la pubblicazione di messaggi Redis, publish ha 2 overloading:
 * publish(String message) : pubblica nel canale di broadcast definito in application.properties
 * publish(String message, ChannelTopic topic): pubblica nel canale "topic" passato come parametro
 * @author luca9
 *
 */
@Slf4j
@Service
public class RedisMessagePublisher implements MessagePublisher {
    private RedisTemplate<String, Notification> redisTemplate;
    private ChannelTopic topic;
  
    public RedisMessagePublisher(RedisTemplate<String, Notification> redisTemplate, ChannelTopic topic) {
      this.redisTemplate = redisTemplate;
      this.topic = topic;
    }
 
    public void publish(Notification message) {
    	log.info("pre    "+message);
        redisTemplate.convertAndSend(topic.getTopic(), message); //serializza con objectmapper default
        
        //TODO
        //object mapper va iniettato e non va fatot il new perchè in questi casi creo 2 istanze differenti
        log.info("msg"+message);
        log.info("canale : {} - {} ", topic.getTopic(), message);
    }
    public void publish(String message, ChannelTopic topic) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
        log.info("canale : {} - {} ", topic.getTopic(), message);
    }
}
