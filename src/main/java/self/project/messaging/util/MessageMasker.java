package self.project.messaging.util;

import org.springframework.lang.NonNull;
import self.project.messaging.dto.MessageDto;

public class MessageMasker {

    public String mask(@NonNull String message) {
        return message.length() > 8 ? message.substring(0, 8) + "***" : message;
    }

    public String mask(@NonNull MessageDto message) {
        message.setContent(mask(message.getContent()));
        return message.toString();
    }
}
