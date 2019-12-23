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

//import it.visualsoftware.notificator.RestTemplate.RestTemplateService;
import it.visualsoftware.notificator.dao.NotificationDao;
import it.visualsoftware.notificator.models.Notification;
import it.visualsoftware.notificator.redis.RedisHash;
import it.visualsoftware.notificator.redis.RedisMessagePublisher;
import it.visualsoftware.notificator.redis.RedisQueueEvictor;
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
	private RedisQueueEvictor queue;
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
		
		int ora= Calendar.getInstance().get(Calendar.HOUR)+12;
		int minuto = Integer.valueOf(min);
		//int min = 35;
		
		log.info("onmessage {} {} ",ora, min);
		//hash.get("expiring");
		for (int i=0; i<10; i++) {
			log.info("pubblico " +i);
			Notification x = new Notification("luca"+i, String.valueOf(i),LocalDateTime.of(2019, Month.DECEMBER, 18, ora,minuto),String.valueOf(i),String.valueOf(i),String.valueOf(i),"token");
			log.info("publish"+x);
			queue.push(x);
			//Thread.sleep(100);
			//publisher.publish(x,new ChannelTopic("evictor"));
		}
		//int i = 0000;
		//return RedisMessageListener.messageQueue.poll();
//		log.info("dim : "+RedisMessageListener.messageQueue.size());
//		Notification x = new Notification("luca", "demo",new Timestamp(14584478), "appuntamento","ecco alle 10","","");
//		Notification notifica = RedisMessageListener.messageQueue.poll();

		//log.info("print before replace "+ RedisMessageListener.messageQueue.size());
//		if (notifica==null){
//			return new Notification(null,null,null,null,null,null,null);
//		}
		
		
//								mail.from("luca.95b@live.it");
//								mail.to("luca.95b@gmail.com");
//								mail.subject(notifica.getTitle());
//								mail.body(notifica.getContent());
//								mail.send();
//								Sms sms = new Sms();
//								sms.sendSms(notifica.getTenant(), notifica.getTitle(), "??", "3341458565", "3382909785", notifica.getContent());
//								template.SendNotification(notifica);
//		return notifica;
		//RedisMessageListener.messageQueue.poll());
		//RedisMessageListener.messageQueue.poll();
		
		
		//return x;
		
		//Notification x =  RedisMessageListener.messageList.poll();
	}
	
	
	@ExceptionHandler (value = {RuntimeException.class,Exception.class })
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
