package project.TelegramBot;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import project.configuration.BotConfig;

@Component
public class CommuteBot extends TelegramLongPollingBot {
    private final BotConfig config;

    @Autowired
    public CommuteBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (message != null && message.hasText()) {
            String messageText = message.getText();
            Long chatId = message.getChatId();

            if ("/start".equals(messageText)) {
                sendStartMessage(chatId);
            } else {
                sendMessage(chatId, "Вы отправили: " + messageText);
            }
        }
    }

    private void sendStartMessage(Long chatId) {
        String startMessage = "Привет! Я бот, который помогает прогнозировать время в пути до работы. \n" +
                "Просто отправь мне свой адрес дома и работы, и я помогу рассчитать оптимальное время для выезда.";

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(startMessage);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

//    private void handleGoWorkCommand(Message message) {
//        // Логика для обработки команды /goWork
//        String command = message.getText().substring(7).trim(); // Убираем "/goWork"
//        String[] parts = command.split(" ");
//        if (parts.length == 3) {
//            String homeAddress = parts[0];
//            String workAddress = parts[1];
//            String workStartTime = parts[2];
//
//            UserDTO commuteRequestDTO = new UserDTO();
//            commuteRequestDTO.setHomeAddress(homeAddress);
//            commuteRequestDTO.setWorkAddress(workAddress);
//            commuteRequestDTO.setWorkStartTime(parseTime(workStartTime));
//
//            RestTemplate restTemplate = new RestTemplate();
//            String url = "http://localhost:8080/api/commute/goWork";
//            try {
//                CommuteTime response = restTemplate.postForObject(url, commuteRequestDTO, CommuteTime.class);
//                sendMessage(message, "Спасибо за информацию! Мы рассчитали оптимальное время для выезда.");
//            } catch (Exception e) {
//                e.printStackTrace();
//                sendMessage(message, "Произошла ошибка при расчете времени. Попробуйте позже.");
//            }
//        }
//    }
//    private Date parseTime(String time) {
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//        try {
//            // Парсим время и добавляем текущую дату
//            Date parsedTime = sdf.parse(time);
//            return parsedTime;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//

}
