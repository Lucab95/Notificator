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

//import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import it.visualsoftware.notificator.models.Notification;
import it.visualsoftware.notificator.redis.MessagePublisher;
import it.visualsoftware.notificator.redis.RedisMessageListener;
import it.visualsoftware.notificator.redis.RedisMessagePublisher;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RedisConfiguration {
	private final String broadChannel;
	ObjectMapper mapper;
	public RedisConfiguration(@Value("${channel.broad}") String broadChannel, ObjectMapper mapper) {
		this.broadChannel=broadChannel;
		this.mapper=mapper;
		//DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
		//this.objectMapper= objectMapper.registerModule(new JavaTimeModule());
	}
	 
	@Bean
	 public RedisTemplate<String, Object> getObjectRedisTemplate(RedisConnectionFactory redisConnectionFactory){
    	RedisTemplate<String, Object> template = new RedisTemplate<String,Object>();
    	template.setConnectionFactory(redisConnectionFactory);
    	template.setKeySerializer(new StringRedisSerializer());
    	template.setValueSerializer(new GenericJackson2JsonRedisSerializer(mapper));
    	template.setHashKeySerializer(new StringRedisSerializer());
    	template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(mapper));
    	return template;
    }
	
	@Bean
	MessageListenerAdapter messageListener(RedisTemplate <String,Object> redisTemplate) {
	    return new MessageListenerAdapter(new RedisMessageListener(redisTemplate,mapper));
	}
	@Bean
    RedisMessageListenerContainer redisContainer(JedisConnectionFactory jedisConnectionFactory, RedisTemplate <String,Object> redisTemplate) {
		RedisMessageListenerContainer container 
	      = new RedisMessageListenerContainer();
	    container.setConnectionFactory(jedisConnectionFactory);	
	    container.addMessageListener(messageListener(redisTemplate), topic());
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
