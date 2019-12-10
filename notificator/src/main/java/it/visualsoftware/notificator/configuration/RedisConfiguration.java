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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.visualsoftware.notificator.redis.MessagePublisher;
import it.visualsoftware.notificator.redis.RedisMessageListener;
import it.visualsoftware.notificator.redis.RedisMessagePublisher;

@Configuration	
public class RedisConfiguration {
	private final String broadChannel;
	private ObjectMapper objectMapper;
	
	public RedisConfiguration(@Value("${channel.broad}") String broadChannel) {
		this.broadChannel=broadChannel;
		this.objectMapper= new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
	}
	 
	@Bean
	 public RedisTemplate<String, Object> getObjectRedisTemplate(RedisConnectionFactory redisConnectionFactory){
    	RedisTemplate<String, Object> template = new RedisTemplate<>();
    	template.setConnectionFactory(redisConnectionFactory);
    	template.setKeySerializer(new StringRedisSerializer());
    	template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    	template.setHashKeySerializer(new StringRedisSerializer());
    	template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    	return template;
    }
	
	@Bean
	MessageListenerAdapter messageListener() {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
	    return new MessageListenerAdapter(new RedisMessageListener(objectMapper));
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
    MessagePublisher redisPublisher(RedisTemplate <String,Object> redisTemplate) {
        return new RedisMessagePublisher(redisTemplate, topic());
    }
    

    @Bean
    ChannelTopic topic() {
        return new ChannelTopic(broadChannel);
    }
    
    
}
