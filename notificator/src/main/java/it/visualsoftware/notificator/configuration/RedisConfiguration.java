package it.visualsoftware.notificator.configuration;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import it.visualsoftware.notificator.models.Notification;
import it.visualsoftware.notificator.redis.MessagePublisher;
import it.visualsoftware.notificator.redis.RedisMessageListener;
import it.visualsoftware.notificator.redis.RedisMessagePublisher;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RedisConfiguration {
	private final String broadChannel;	
	ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
	.registerModule(new JavaTimeModule());
	public RedisConfiguration(@Value("${channel.broad}") String broadChannel) {
		this.broadChannel=broadChannel;
		//DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
		//this.objectMapper= objectMapper.registerModule(new JavaTimeModule());
	}
	 
	@Bean
	 public RedisTemplate<String, Notification> getObjectRedisTemplate(RedisConnectionFactory redisConnectionFactory){
    	RedisTemplate<String, Notification> template = new RedisTemplate<String,Notification>();
    	template.setConnectionFactory(redisConnectionFactory);
    	template.setKeySerializer(new StringRedisSerializer());
    	template.setValueSerializer(new GenericJackson2JsonRedisSerializer(mapper));
    	template.setHashKeySerializer(new StringRedisSerializer());
    	template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    	return template;
    }
	
	@Bean
	MessageListenerAdapter messageListener() {
	    return new MessageListenerAdapter(new RedisMessageListener(mapper));
	}
	@Bean
    RedisMessageListenerContainer redisContainer(JedisConnectionFactory jedisConnectionFactory) {
		RedisMessageListenerContainer container 
	      = new RedisMessageListenerContainer();
	    container.setConnectionFactory(jedisConnectionFactory);	
	    container.addMessageListener(messageListener(), topic());
	    return container;
    }
	

    @Bean
    MessagePublisher redisPublisher(RedisTemplate <String,Notification> redisTemplate) {
        return new RedisMessagePublisher(redisTemplate, topic());
    }
    

    @Bean
    ChannelTopic topic() {
        return new ChannelTopic(broadChannel);
    }
    
    
}
