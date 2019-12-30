package it.visualsoftware.notificator;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import Exception.NotificationSentException;
//import it.visualsoftware.notificator.RestTemplate.RestTemplateService;
import it.visualsoftware.notificator.dao.NotificationDao;
import it.visualsoftware.notificator.models.Notification;
import it.visualsoftware.notificator.redis.RedisHash;
import it.visualsoftware.notificator.redis.RedisMessagePublisher;
import it.visualsoftware.notificator.models.ErrorInfo;
//import it.visualsoftware.notificator.redis.RedisMessageListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class NotificatorController {
	private final NotificationDao repository;
	@Autowired
	private RedisMessagePublisher publisher;
	@Autowired
	private RedisHash hash;
	@Autowired
	//private RedisQueueEvictor queue;
								//private MailSender mail;
	public NotificatorController(NotificationDao repo) {
//		this.template = template;
		this.repository=repo;
								//this.mail = new MailSender();
	}
	
	@PostMapping("/create")
	public void populate(@RequestBody Notification notify) {
		log.info("notify");
		repository.insertNotification(notify);
	}
	
	@GetMapping("/all")
	public List<Notification> getAll(){
		log.info("print");
		return repository.findall();
	}
	
	//TODO stampare messaggio
	@GetMapping("/message")
	public void queue(@RequestBody String min/*String chann*/) throws InterruptedException {
		//TODO ORA IN formato 24
		Calendar time= Calendar.getInstance();
		int ora = time.get(Calendar.HOUR_OF_DAY);
		int minuto = Integer.valueOf(min);
		//int min = 35;
		
		log.info("onmessage {} {} ",ora, min);
		//hash.get("expiring");
		for (int i=0; i<10; i++) {
			log.info("pubblico " +i);
			Notification x = new Notification("luca"+i, String.valueOf(i),LocalDateTime.of(2019, Month.DECEMBER, 30, ora,minuto),String.valueOf(i),String.valueOf(i),String.valueOf(i),"token", "insert");
			log.info("publish"+x);
			//queue.push(x);
			//Thread.sleep(100);
			publisher.publish(x,new ChannelTopic("evictor"));
		}
	}
	@PostMapping("/delete")
	public void delete(@RequestBody Notification notify) throws InterruptedException {
		log.info("notify remove {} " ,notify);
		hash.remove("pari", "13", new Notification(notify.getUsr(), notify.getTenant(),LocalDateTime.of(2019, Month.DECEMBER, 30, 16,13),notify.getTitle(),notify.getContent(),notify.getUrl(),"token","remove"));
//		Notification x = new Notification(notify.getUsr(), notify.getTenant(),LocalDateTime.of(2019, Month.DECEMBER, 30, 12,41,36,156),notify.getTitle(),notify.getContent(),notify.getUrl(),"token", "remove");
//		publisher.publish(x ,new ChannelTopic("evictor"));
		}
	
	
	@ExceptionHandler (value = {RuntimeException.class,Exception.class,NotificationSentException.class})
	@ResponseBody 
	public ResponseEntity<ErrorInfo> handleUnknownError(HttpServletRequest req ,Exception ex){
		String errorMessage = ex != null ? ex.getMessage() : "";
		String errorUID = (UUID.randomUUID().toString());
		errorUID = errorUID.substring(errorUID.length()-8);
		log.error("({}) {}",errorUID,"eccezione generica", ex);
		ErrorInfo err = new ErrorInfo(HttpStatus.INTERNAL_SERVER_ERROR.value(),errorUID,errorMessage,new Date());
		return new ResponseEntity<ErrorInfo>(err, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
	

}
