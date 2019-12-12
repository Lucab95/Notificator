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

	@Override
	public List<Notification> nextMinutes(long interval) {
		interval=interval*60000;
		long nowMillis = new Date().getTime();
		Timestamp now = new Timestamp(nowMillis);
		now.setNanos(0);
		log.info("interval"+ interval +"  "+nowMillis);
		long afterMillis = now.getTime() + interval;
 		Timestamp after = new Timestamp(afterMillis);
		log.info("from min "+ now + " to " + after );
		String sql ="SELECT * FROM notification WHERE end_date BETWEEN '" +now+ "' AND '" +after+ "'";
		return template.query(sql, new NotificationRowMapper());
		
		//publish su redis
	}
	
	@Override
	public List<Notification> nextHour(long interval) {
		Calendar now = Calendar.getInstance();
		int startHour = now.get(Calendar.HOUR);
		//now.set(Calendar.MILLISECOND, 0);
		now.set(Calendar.SECOND,0);
		now.set(Calendar.MINUTE,0);
		now.set(Calendar.HOUR,startHour+1);
		Calendar after = (Calendar) now.clone();
		
		after.set(Calendar.HOUR,startHour+2);
		Timestamp start = new Timestamp(now.getTimeInMillis());
		Timestamp end = new Timestamp(after.getTimeInMillis());
//		now.setNanos(0);
//		now.setMinutes(0);
//		now.setSeconds(0);
//		log.info("interval"+ interval +"  "+nowMillis);
//		long afterMillis = now.getTime() + interval;
// 		Timestamp after = new Timestamp(afterMillis).getHours();
		log.info("from min "+ start + " to " + end );
		String sql ="SELECT * FROM notification WHERE end_date BETWEEN '" + start     + "' AND '" +end+ "'";
		return template.query(sql, new NotificationRowMapper());
	}
	
}
