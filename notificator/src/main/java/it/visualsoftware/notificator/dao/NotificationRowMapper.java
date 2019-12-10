package it.visualsoftware.notificator.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;

import it.visualsoftware.notificator.models.Notification;

public class NotificationRowMapper implements RowMapper<Notification>  {

	//jdbc to map from sql to java Object
	@Override
	public Notification mapRow(ResultSet rs, int rowNum) throws SQLException {
		Notification notification = new Notification();
		notification.setUsr(rs.getString("usr"));
		notification.setTenant(rs.getString("tenant"));
		notification.setEndDate(rs.getTimestamp("end_date").toLocalDateTime());// convert to long
		notification.setTitle(rs.getString("title"));
		notification.setContent(rs.getString("content"));
		notification.setUrl(rs.getString("url"));
		return notification;
	}

}
