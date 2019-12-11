package it.visualsoftware.notificator;

import java.util.List;


import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import it.visualsoftware.notificator.RestTemplate.RestTemplateService;
import it.visualsoftware.notificator.dao.NotificationDao;
import it.visualsoftware.notificator.models.Notification;
import it.visualsoftware.notificator.redis.RedisMessageListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class NotificatorController {
	private final NotificationDao repository;
	//private SendResult send;
	private final RestTemplateService template;
	//private MailSender mail;
	public NotificatorController(NotificationDao repo, RestTemplateService template) {
		this.template = template;
		this.repository=repo;
		//this.send=send;
		//this.mail =mail;
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
	@Async
	@GetMapping("/message")
	public String queue() throws JsonMappingException, JsonProcessingException{
		log.info("info");
		//return RedisMessageListener.messageQueue.poll();
		//log.info(""+RedisMessageListener.messageQueue.poll());
		//log.info("blpop"+RedisMessageListener.jedisQueue.blpop(0,"queue"));
		//mail.attach("ciao");
		//send.isOk();
		//Notification x = new Notification("luca", "demo",new Timestamp(14584478), "appuntamento","ecco alle 10","","");
		//String string = RedisMessageListener.jedisQueue.lpop("queue");
		Notification string = RedisMessageListener.messageQueue.poll();
		log.info("print before replace "+ string);
		if (string==null){
			return "queue empty";
		}
//		string = string.replace("\\","");
//		string = string.replace("\"{","{");
//		string = string.replace("}\"","}");
		
		log.info("print after replace \n   "+string.toString());
		
//		ObjectMapper objectMapper = new ObjectMapper();
//		Notification x = objectMapper.readValue(string, Notification.class);
		template.SendNotification(string);
		//return string;
		return string.toString();
		//RedisMessageListener.messageQueue.poll());
		//RedisMessageListener.messageQueue.poll();
		
		
		//return x;
		
		//Notification x =  RedisMessageListener.messageList.poll();
	}
}
