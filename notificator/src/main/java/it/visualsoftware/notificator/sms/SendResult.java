package it.visualsoftware.notificator.sms;

import lombok.Data;

@Data
public class SendResult {
	
	private final String error;
	private final boolean ok;
	
	public static final SendResult OK(){
		return new SendResult(null, true);
	}
	
	public static final SendResult ERROR(String err){
		return new SendResult(err, false);
	}
	
	

}
