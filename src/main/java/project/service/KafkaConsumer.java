package project.service;


import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import project.dto.BotCommandDTO;
import project.dto.NotificationDTO;


@Service
public class KafkaConsumer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);
    private final ParseService parseService;
    private final UserService userService;
    private final KafkaTemplate<Long, NotificationDTO> kafkaTemplate;

    @Autowired
    public KafkaConsumer(ParseService parseService, UserService userService,
                         KafkaTemplate<Long, NotificationDTO> kafkaTemplate) {
        this.parseService = parseService;
        this.userService = userService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "producer-bot-topic", groupId = "service-group")
    public void handleCommand(BotCommandDTO dto) {
        logger.info("Получена команда от бота: {}", dto);

        try {
            switch (dto.getCommand()) {
                case "start" -> parseService.parseAndSaveUser(dto.getTelegramId() + "; " + dto.getMessage());
                case "go_to_work" -> {
                    parseService.parseAndSave(dto.getTelegramId(), dto.getMessage());
                    sendConfirmation(dto.getTelegramId(), "Отправим уведомление за 30 минут до выезда!");
                }
                case "stop" -> userService.disableNotifications(dto.getTelegramId());
                default -> logger.warn("Неизвестная команда: {}", dto.getCommand());
            }
        } catch (JSONException e) {
            logger.error("Ошибка парсинга JSON: {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Ошибка обработки команды: {}", e.getMessage(), e);
        }
    }

    private void sendConfirmation(Long telegramId, String text) {
        NotificationDTO confirmation = new NotificationDTO();
        confirmation.setTelegramUserId(telegramId);
        confirmation.setMessage(text);
        confirmation.setNotifyTime(null);

        kafkaTemplate.send("confirmations", telegramId, confirmation);
        logger.info("Отправлено подтверждение в Kafka 'confirmations': {}", confirmation);
    }
}
