package mate.academy.carsharing.service.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final CarSharingBot carSharingBot;

    @Value("${telegram.bot.chat.id}")
    private String chatId;

    @Autowired
    public NotificationService(CarSharingBot carSharingBot) {
        this.carSharingBot = carSharingBot;
    }

    @Async
    public void sendNotification(String message) {
        carSharingBot.sendMessage(message, Long.valueOf(chatId));
    }
}
