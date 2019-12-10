package it.visualsoftware.notificator.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class Notification {
	
	//not null
	private String usr;
	//not null
	private String tenant;
	//not null
	private long endDate;
	private String title;
	private String content;
	private String url;
	private String token;
	
	public Notification() {}
	public Notification(String usr, String tenant,long end, String title, String content, String url, String token) {
		this.usr = usr;
		this.tenant = tenant;
		this.endDate=end;
		this.title=title;
		this.content=content;
		this.url = url;
		this.token=token;
	}
}
