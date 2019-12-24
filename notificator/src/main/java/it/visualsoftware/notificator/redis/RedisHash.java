package it.visualsoftware.notificator.redis;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
//import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.visualsoftware.notificator.RestTemplate.RestTemplateService;
import it.visualsoftware.notificator.models.Notification;
import lombok.extern.slf4j.Slf4j;

//********STRUTTURA HASH PER GESTIONE NOTIFICHE********
@Slf4j
public class RedisHash {
	protected final RedisTemplate<String,Object> redis;
	protected final ObjectMapper mapper;
	
	
	public RedisHash(RedisTemplate<String,Object> redis, ObjectMapper mapper ) {
		this.redis=redis;
		this.mapper=mapper;
	}
	
	/**
	 * Inserisce la notifica nella struttura per la schedulazione già esistente
	 * @param hashName
	 * @param key
	 * @param notify
	 */
	public void add(String hashName, String key , Notification notify) {
		log.info("lock");
		redis.opsForList().rightPop("lock",5, TimeUnit.SECONDS);
//		while (redis.opsForList().size("lock")>0) {
//			redis.opsForList().rightPop("lock");
//		}
		try {
			List<Notification> inThisMin;
			Object map = redis.opsForHash().get(hashName,key);
			if (map!=null) {
				inThisMin= mapper.convertValue(map, new TypeReference<List<Notification>>(){});
			} else {
				inThisMin= new ArrayList<Notification>();
			}
			if (!(inThisMin.contains(notify))) {
				inThisMin.add(notify);
				redis.opsForHash().put(hashName, key, inThisMin);
					log.info("oldlist  {} ",inThisMin.size());
				log.info("notification {} scheduled for min {} ",notify.getTitle(), key);
			}else {
				log.info("già presente");
			}
		}catch(Exception ex){
			log.info("error on inserting notification");
		}finally {
			redis.opsForList().leftPush("lock", "1");
			log.info("unlock");
		}
	}
	
	/**
	 * A seguito di un annullamento, rimuove la notifica già schedulata 
	 * @param hashName
	 * @param key
	 * @param notify
	 */
	public void remove(String hashName, String key , Notification notify) {
		log.info("lock");
		redis.opsForList().rightPop("lock",5, TimeUnit.SECONDS);
		try{
			List<Notification> inThisMin;
			Object map = redis.opsForHash().get(hashName,key);
			if (map!=null) {
				inThisMin= mapper.convertValue(map, new TypeReference<List<Notification>>(){});
				log.info("object removed {}",inThisMin.remove(notify));
				redis.opsForHash().put(hashName, key, inThisMin);
			}
		}catch(Exception ex){
			log.info("error on removing scheduled notification");
		}finally {
			redis.opsForList().leftPush("lock", "1");
			log.info("unlock");
		}
	}
	
	/**
	 *  Nel caso di rischedulazione, toglie la notifica dalla schedulazione gia fatta. 
	 *  Se è ancora nella stessa fascia oraria, lo reinserisce
	 * @param hashName
	 * @param key
	 * @param notifyOld
	 * @param notifyNew
	 */
	public void reSchedule(String hashName, String key, Notification notifyOld, Notification notifyNew) {
		log.info("lock");
		redis.opsForList().rightPop("lock",5, TimeUnit.SECONDS);
		try {
			List<Notification> inThisMin;
			Object map = redis.opsForHash().get(hashName,key);
			if (map!=null) {
				inThisMin= mapper.convertValue(map, new TypeReference<List<Notification>>(){});
				log.info("object removed {}",inThisMin.remove(notifyOld));
				//se notifyNew.getEndDate in questa fascia oraria
				if (notifyOld.getEndDate().getHour() == notifyNew.getEndDate().getHour()) {
					inThisMin.add(notifyNew);
				}
				redis.opsForHash().put(hashName, key, inThisMin);
			}
		}catch(Exception ex){
			log.info("error on rescheduling");
		}finally {
			redis.opsForList().leftPush("lock", "1");
		}
		
	}
	
	//la get viene utilizzata per ritornare la lista con tutte le notifiche da inviare in questo momento
	public List<Notification> get(String hashName, String key) {
		log.info("try get");
		Object map = redis.opsForHash().get(hashName,key);
		if (map==null) {
			log.info("no keys found");
			return new ArrayList<Notification>();
		}
		List<Notification> minuteList = mapper.convertValue(map, new TypeReference<List<Notification>>(){});
		return Collections.unmodifiableList(minuteList);
	}
	
	//svuota la lista, prima di ripopolarla per la prossima ora 
	public void flush(String hashName) {
		log.info("deleted"+hashName);
		redis.delete(hashName);
	}

//	public void putSet(String hashName, int min, RedisSet set) {
//		List<Notification> x = get(hashName,min);
//		Set<Object> y = set.members();
//		Iterator<Object> value = y.iterator();
//		while(value.hasNext()) {
//			Notification obj = mapper.convertValue(value.next(), Notification.class);
//			if (!(x.contains(obj))){
//			x.add(obj);
//		}
//		}
//		put(hashName,min,x);
//	}
}
