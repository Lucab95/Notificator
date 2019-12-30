package it.visualsoftware.notificator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
public class NotificatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificatorApplication.class, args);
	}

}
