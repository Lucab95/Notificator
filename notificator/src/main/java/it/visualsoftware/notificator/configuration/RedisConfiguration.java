package it.visualsoftware.notificator.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.visualsoftware.notificator.RestTemplate.RestTemplateService;
import it.visualsoftware.notificator.redis.MessagePublisher;
import it.visualsoftware.notificator.redis.RedisHash;
import it.visualsoftware.notificator.redis.RedisMessageListenerEvictor;
import it.visualsoftware.notificator.redis.RedisMessagePublisher;
import it.visualsoftware.notificator.redis.RedisQueueEx;
import it.visualsoftware.notificator.redis.RedisSet;

@Configuration
public class RedisConfiguration {
	private final String evictorChannel;
	private ObjectMapper mapper;
	private final RestTemplateService template;
	public RedisConfiguration(@Value("${channel.evictor}") String evictorChannel, ObjectMapper mapper, RestTemplateService template) {
		this.evictorChannel=evictorChannel;
		this.mapper=mapper;
		this.template=template;
	}
	 
	@Bean
	 public RedisTemplate<String, Object> getObjectRedisTemplate(RedisConnectionFactory redisConnectionFactory){
    	RedisTemplate<String, Object> template = new RedisTemplate<String,Object>(); 
    	template.setConnectionFactory(redisConnectionFactory);
    	template.setKeySerializer(new StringRedisSerializer());
    	template.setValueSerializer(new GenericJackson2JsonRedisSerializer(mapper));
    	template.setHashKeySerializer(new StringRedisSerializer());
    	template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(mapper));
    	template.setEnableTransactionSupport(true); 
    	return template;
    }
	
	@Bean 
	RedisQueueEx getQueue(RedisTemplate <String,Object> redisTemplate) {
		RedisQueueEx queue = new RedisQueueEx(redisTemplate, "queue");
		return queue;
	}
//	@Bean 
//	RedisQueueEvictor getQueueEvents(RedisTemplate <String,Object> redisTemplate) {
//		RedisQueueEvictor queueEvents = new RedisQueueEvictor(redisTemplate,"events_queue");
//		return queueEvents;
//	}
	
	/**
	 * RedisHash per notifiche schedulate
	 * @param redisTemplate
	 * @return hash
	 */
	@Bean
	RedisHash getHash(RedisTemplate<String, Object> redisTemplate) {
		RedisHash hash = new RedisHash(redisTemplate,mapper);
		return hash;
		
	}

	//RedisSet per notifiche da modificare se la modifiche è nella prossima ora con Evictor
	@Bean
	RedisSet getSet(RedisTemplate<String, Object> redisTemplate) {
		RedisSet set = new RedisSet(redisTemplate,mapper,"changesSet");
		return set;
		
	}
	
//	@Bean
//	MessageListenerAdapter messageListener(RedisQueueEx queue) {
//		queue.listener(mapper,template);
//	    return new MessageListenerAdapter(new RedisMessageListener(mapper,queue));
//	}
	
	@Bean
	MessageListenerAdapter messageListener(RedisQueueEx queue,RedisSet set) {
		
	    return new MessageListenerAdapter(new RedisMessageListenerEvictor(mapper, set));
	}
	
	
	@Bean
    RedisMessageListenerContainer redisContainer(JedisConnectionFactory jedisConnectionFactory,RedisQueueEx queue,
    		RedisSet set){//RedisQueueEvictor queueEvents ){
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
	    container.setConnectionFactory(jedisConnectionFactory);	
	    //container.addMessageListener(messageListener(queue), topic());
	    //queueEvents.listener(mapper,template);
	    queue.listener(mapper,template);
	    container.addMessageListener(messageListener(queue,set),topic());
	    return container;
    }
	
	

    @Bean
    MessagePublisher redisPublisher(RedisTemplate <String,Object> redisTemplate) {
        return new RedisMessagePublisher(redisTemplate, topic());
    }
    
    
    @Bean
    ChannelTopic topic() {
        return new ChannelTopic(evictorChannel);
    }
    
    
}
