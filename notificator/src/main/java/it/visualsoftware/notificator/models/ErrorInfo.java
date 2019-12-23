package it.visualsoftware.notificator.models;
import java.util.Date;
import lombok.Data;

public @Data class ErrorInfo {
	private Integer status;
	private String logHook;
	private String reason;
	private Date date;
	
	public ErrorInfo(int i,String logHook, String errorMessage,  Date date) {
		this.reason=errorMessage;
		this.logHook=logHook;
		this.status=i;
		this.date= date;
	}
}

