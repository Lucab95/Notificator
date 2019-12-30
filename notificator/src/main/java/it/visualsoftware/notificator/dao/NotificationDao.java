package it.visualsoftware.notificator.dao;

import java.util.List;

import it.visualsoftware.notificator.models.Notification;

public interface NotificationDao {

	List<Notification> findall();
	
	void insertNotification(Notification notify);
//	void removeNotification(Notification notify);
	
	//List<Notification> nextMinutes(long interval);
	
	List<Notification> nextHour(long interval);

}
