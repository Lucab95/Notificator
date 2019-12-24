package Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_GATEWAY, reason="Bad Gateway") //502
public class NotificationSentException extends Exception {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1798677109205395327L;

	public NotificationSentException(int i) {
			super("message not sent with error code : "+i);
		}
}
