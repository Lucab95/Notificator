package it.visualsoftware.notificator.sms;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Sms implements ISenderSms{


	@Override
	public SendResult sendSms(String tenantId, String username, String password, String from, String to, String text) {
		// TODO Auto-generated method stub
		log.info("invia messaggio a {}",to);
		return null;
	}

}
