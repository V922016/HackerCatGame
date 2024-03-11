package ua.javarush;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.google.common.base.Utf8;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static ua.javarush.TelegramBotContent.*;
import static ua.javarush.TelegramBotUtils.*;

public class MyFirstTelegramBot extends TelegramLongPollingBot {
    private final Map<Long,String> chatIdToUserName=new HashMap<>();
    private final Set<Long> awaitingName = new HashSet<>(); // Для отслеживания, кто должен ввести имя

    @Override
    public String getBotUsername() {
        // TODO: додай ім'я бота в лапки нижче
        return "WhiteHackerCatBot";
    }

    @Override
    public String getBotToken() {
        // TODO: додай токен бота в лапки нижче
        return "";
    }

    @Override
    public void onUpdateReceived(Update update) {
        // TODO: основний функціонал бота будемо писати тут
        Long ChatId = getChatId(update);
        //Checking, is the text message exists?
        if (!update.hasMessage()||!update.getMessage().hasText()) return;
        String incomingText = update.getMessage().getText();
        SendMessage message = new SendMessage(); 
        message.enableHtml(true); // Setting HTML formatting
        message.setChatId(String.valueOf(ChatId));

        // Processing command /start
    if (incomingText.equals("/start")) {
        message.setText("Привiт, майбутнiй програмiст! Як тебе звуть?");
        awaitingName.add(ChatId); // We note that we're waiting for the username
    } else if (awaitingName.contains(ChatId)) {
        // The user enters his name
        chatIdToUserName.put(ChatId, incomingText); // Saving the username
        message.setText("Приємно познайомитися, " + incomingText + ", я - <b>Кiт</b>");
        awaitingName.remove(ChatId); // Removing the wait mark
    } else {
        // For any other message (not /start)
        message.setText("Радий тебе бачити знову, " + chatIdToUserName.getOrDefault(ChatId, "незнайомець") + "!");
    }

        try {
            execute(message); //Sending the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        
    }

    private Long getChatId(Update update) {
        return update.getMessage().getChatId();
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new MyFirstTelegramBot());
    }
}