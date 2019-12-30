package it.visualsoftware.notificator.dao;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import it.visualsoftware.notificator.models.Notification;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class NotificationDaoImpl implements NotificationDao {
	private NamedParameterJdbcTemplate template;
	
	public NotificationDaoImpl(NamedParameterJdbcTemplate temp) {
		this.template= temp;
	}
	
	@Override
	public List<Notification> findall() {
		return template.query("select * from notification", new NotificationRowMapper());
		
	}

	@Override
	public void insertNotification(Notification notify) {
		final String sql= "insert into notification(usr,tenant,title,content,url,end_date) values(:usr,:tenant,:title,:content,:url,:end_date)";
		KeyHolder holder = new GeneratedKeyHolder();
        SqlParameterSource param = new MapSqlParameterSource()
								.addValue("usr", notify.getUsr())
								.addValue("tenant", notify.getTenant())
								.addValue("title", notify.getTitle())
								.addValue("end_date", notify.getEndDate())
								.addValue("content", notify.getContent())
								.addValue("url", notify.getUrl());
        template.update(sql,param, holder);
        log.info("holder is ()" +holder.toString());
        
	}
	
//	@Override
//	public void removeNotification(Notification notify) {
//		final String sql = "delete from notification(usr, tenant, end_date) values (:usr,:tenant,:end_date)";
//		SqlParameterSource param = new MapSqlParameterSource()
//				.addValue("usr", notify.getUsr())
//				.addValue("tenand", notify.getTenant())
//				.addValue("end_date", notify.getEndDate());
//		log.info("deleted {}", template.update(sql, param));
//		
//	}

	
	@Override
	public List<Notification> nextHour(long interval) {
		Calendar now = Calendar.getInstance();
		int startHour = 14;//now.get(Calendar.HOUR);
		log.info("start"+startHour);
		now.set(Calendar.MILLISECOND,00);
		now.set(Calendar.DAY_OF_MONTH, 24);
		now.set(Calendar.SECOND,00);
		now.set(Calendar.MINUTE,00);
		now.set(Calendar.YEAR, 2019);
		now.set(Calendar.HOUR_OF_DAY, 14);//startHour+1);//7:50->8:00
		Calendar after = (Calendar) now.clone();
		
		after.set(Calendar.HOUR_OF_DAY,startHour+3);//8->9
		Timestamp start = new Timestamp(now.getTimeInMillis());
		Timestamp end = new Timestamp(after.getTimeInMillis());
//		now.setNanos(0);
//		now.setMinutes(0);
//		now.setSeconds(0);
//		log.info("interval"+ interval +"  "+nowMillis);
//		long afterMillis = now.getTime() + interval;
// 		Timestamp after = new Timestamp(afterMillis).getHours();
		log.info("from min "+ start + " to " + end );
		String sql ="select assignee as usr, 'dtidona' as tenant, calendar_date_start as end_date, 'Appuntamento' as title, description as content, null as url from dtidona.activity_view WHERE calendar_date_start BETWEEN '" + start     + "' AND '" +end+ "' ORDER BY calendar_date_start ASC";
		return template.query(sql, new NotificationRowMapper());
	}

	
}
