package it.visualsoftware.notificator;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import it.visualsoftware.notificator.RestTemplate.RestTemplateService;
import it.visualsoftware.notificator.dao.NotificationDao;
import it.visualsoftware.notificator.models.Notification;
import it.visualsoftware.notificator.redis.RedisHash;
import it.visualsoftware.notificator.redis.RedisMessagePublisher;
import it.visualsoftware.notificator.redis.RedisQueueEvents;
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
	private RedisQueueEvents queue;
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
	public void queue(@RequestBody String chann) throws InterruptedException{
		
		int ora= Calendar.getInstance().get(Calendar.HOUR);
		int min = 27;
		
		log.info("info");
		//hash.get("expiring");
		for (int i=0; i<10; i++) {
			
			log.info("pubblico " +i);
			Notification x = new Notification("luca"+i, "demo"+i,LocalDateTime.of(2019, Month.DECEMBER, 18, 18,min),"inserito"+i,"content"+i,"url"+i,"token");
			log.info("publish"+x);
			
			queue.push(x);
			//publisher.publish(x,new ChannelTopic(chann));
		}
		//int i = 0000;
		//RedisMessageListener.jedis.zcard("queue");
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
	
	
	

}
