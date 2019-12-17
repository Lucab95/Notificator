package it.visualsoftware.notificator.RestTemplate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import it.visualsoftware.notificator.models.Notification;

@Service
public class RestTemplateService {

	private final RestTemplate template;
	
	public RestTemplateService(RestTemplate template) {
		this.template=template;
	}
	
	public ResponseEntity<String> SendNotification(Notification notify) {
		String uri = "http://europe-west1-leadmanager-notification.cloudfunctions.net/notify";//endpoiint
		notify.setToken("c6MXZlyo7ss:APA91bFSlYVjYw6xS8L0rz9ZzfacjOi9HTCR54xVFPDUONr7VNSKT0_XkgrQyAUxbCHcJJ8iHBBTCJkSeY13twzvtUjMt_kVQzNAZ3yannBzpjbtkxvWB-i_kdnemS5yubWiUw7K_6s0");
		notify.setTitle("appuntamento di " +notify.getTitle());
		notify.setContent(notify.getContent() + "delle ore "+ notify.getEndDate());
		HttpHeaders headers = new HttpHeaders();
		HttpMethod httpMethod = HttpMethod.valueOf("POST");
		headers.set("Authorization","Bearer zgmvsCuoSrfOQMBfnc8i");
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Notification> req = new HttpEntity<Notification>( notify , headers);
		ResponseEntity<String> response = template.exchange( uri , httpMethod , req , String.class);
		return(response);
	}
}
