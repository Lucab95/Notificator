package it.visualsoftware.notificator.sms;

public interface ISenderSms {
	
	SendResult sendSms(String tenantId,String username,String password,String from, String to, String text);

}
