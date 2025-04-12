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
import project.DTO.UserDTO;
import project.DTO.UserDetailsDTO;
import project.configuration.BotConfig;
import project.model.CommuteTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class CommuteBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final RestTemplate restTemplate;
    private final Map<Long, Boolean> userWaitingForData = new HashMap<>();

    @Autowired
    public CommuteBot(BotConfig config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (message != null && message.hasText()) {
            String messageText = message.getText();
            Long chatId = message.getChatId();

            if ("/start".equals(messageText)) {
                sendStartMessage(chatId);
                saveUserData(message);
            } else if ("/GoToWork".equals(messageText)) {
                sendGoToWorkMessage(chatId);
                userWaitingForData.put(chatId, true);
            } else if (userWaitingForData.containsKey(chatId) && userWaitingForData.get(chatId)) {
                saveUserDataWork(message);
                userWaitingForData.put(chatId, false);
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

    private void saveUserData(Message message) {
        UserDetailsDTO userDetailsDTO = new UserDetailsDTO(
                message.getFrom().getId(),
                message.getFrom().getUserName(),
                message.getFrom().getFirstName()
        );
        String url = "http://localhost:8080/api/commute/start";
        restTemplate.postForObject(url, userDetailsDTO, String.class);
    }

    private void sendGoToWorkMessage(Long chatId) {
        String requestMessage = "Пожалуйста, отправьте адрес дома, адрес работы и время, к которому нужно быть на работе в формате 'HH:mm'. Например: \n" +
                "'Москва, Красная площадь 1; Москва, Тверская улица 7; 09:00'";

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(requestMessage);
        try {
            execute(message); // Отправляем запрос
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void saveUserDataWork(Message message) {
        String userInput = message.getText().trim();

        // Разделяем строку по точке с запятой
        String[] parts = userInput.split(";");

        if (parts.length == 3) {
            String homeAddress = parts[0].trim();
            String workAddress = parts[1].trim();
            String workStartTimeStr = parts[2].trim();
            Date workStartTime = parseTime(workStartTimeStr);

            UserDTO userDTO = new UserDTO();
            userDTO.setTelegramUserId(message.getFrom().getId());
            userDTO.setHomeAddress(homeAddress);
            userDTO.setWorkAddress(workAddress);
            userDTO.setWorkStartTime(workStartTime);
            String url = "http://localhost:8080/api/commute/goToWork";
            RestTemplate restTemplate = new RestTemplate();
            try {
                restTemplate.postForObject(url, userDTO, String.class);
                sendMessage(message.getChatId(), "Данные сохранены, расчет времени выполнен.");
            } catch (Exception e) {
                e.printStackTrace();
                sendMessage(message.getChatId(), "Произошла ошибка при расчете времени.");
            }
        } else {
            sendMessage(message.getChatId(), "Неверный формат времени. Пожалуйста, укажите время в формате HH:mm.");
        }
    }

    private Date parseTime(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            return sdf.parse(time);  // Преобразуем строку времени в Date
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
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
}
