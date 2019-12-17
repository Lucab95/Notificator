package it.visualsoftware.notificator;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

//import com.fasterxml.jackson.databind.ObjectMapper;

import it.visualsoftware.notificator.dao.NotificationDao;
import it.visualsoftware.notificator.models.Notification;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest()
//@WebMvcTest(NotificatorController.class)
@Slf4j
class NotificatorApplicationTests {
	//private MockMvc mvc;
	@Autowired
	private NotificationDao repository;
	
	/*public NotificatorApplicationTests(NotificationDao repository) {
		// TODO Auto-generated constructor stub
		this.repository=repository;
	}*/

	
	@Test
	public void populate() throws Exception{
		long millis = new Date().getTime();
		for (int i=3000;i<4000;i++) {
		//NotifyContent notify = new NotifyContent("prova"+i,"content"+i,"url"+i);
		repository.insertNotification(new Notification("luca"+i, "demo"+i,
								LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), TimeZone.getDefault().toZoneId()),
								"prova"+i,"content"+i,"url"+i,"token" ));
		millis=millis+new Random().nextInt(240000)+120000; //from 2 min to 7
		/*mvc.perform(MockMvcRequestBuilders
			      .post("/create")
			      .content(asJsonString(new Notification("luca", "demo4", new NotifyContent("prova4","content","url"))))
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
			      .andExpect(status().isCreated());
		*/
		
		}
		log.info("inserimento completato");

	}
	
	@Test
	public void getat5() {
		//List<Notification> x = repository.nextMinutes(300000);
		//log.info("print \n" +x.toString());
	}

}
