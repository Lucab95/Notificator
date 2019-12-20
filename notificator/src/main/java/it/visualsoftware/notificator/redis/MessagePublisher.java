package it.visualsoftware.notificator.redis;

import org.springframework.data.redis.listener.ChannelTopic;

import it.visualsoftware.notificator.models.Notification;

public interface MessagePublisher {
	void publish(Notification message);
	void publish(Notification message, ChannelTopic topic);
}
