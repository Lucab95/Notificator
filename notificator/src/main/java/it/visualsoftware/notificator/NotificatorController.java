package it.visualsoftware.notificator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//
//import it.visualsoftware.notificator.RestTemplate.RestTemplateService;
import it.visualsoftware.notificator.dao.NotificationDao;
import it.visualsoftware.notificator.models.Notification;
import it.visualsoftware.notificator.redis.RedisHash;
//import it.visualsoftware.notificator.redis.RedisMessageListener;
//import it.visualsoftware.notificator.sms.Sms;
import lombok.extern.slf4j.Slf4j;
//import it.visual.mailutils.impl.MailSender;

@Slf4j
@RestController
@RequestMapping("/api")
public class NotificatorController {
	private final NotificationDao repository;
	@Autowired
	private RedisHash hash;
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
	public void queue(){
		log.info("info");
		//hash.get("expiring");
		hash.flush("pari");
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
