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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ua.javarush.TelegramBotContent.*;
import static ua.javarush.TelegramBotUtils.*;
import io.github.cdimascio.dotenv.Dotenv;  //Setting the opportunity to use .env files  

public class MyFirstTelegramBot extends TelegramLongPollingBot {
    private final Map<Long,String> chatIdToUserName=new HashMap<>();
    private final Set<Long> awaitingName = new HashSet<>(); // For tracking of who must to enter a name
    //For increased bot variability we're using the list of popular greetings now 
    List<String> greetings = Arrays.asList("привет", "прив", "хай", "ку", "привiт", "вiтаю", "cześć","hello", "hi", "hey", "hola", "bonjour", "ciao", "hallo");
       
    @Override
    public String getBotUsername() {
        // додай ім'я бота в лапки нижче 
        Dotenv dotenv = Dotenv.load();       
        String botUsername = dotenv.get("BOT_USRNAME"); //For increased safety we're not using the direct bot username anymore      
        return botUsername;
    }

    @Override
    public String getBotToken() {
        // додай токен бота в лапки нижче
        Dotenv dotenv = Dotenv.load();
        String botToken = dotenv.get("BOT_TOKEN");  //For increased safety we're not using the direct bot token anymore        
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // основний функціонал бота будемо писати тут
        Long ChatId = getChatId(update);              
        SendMessage message = new SendMessage(); 
        message.enableHtml(true); // Setting HTML formatting
        //Checking, is the text message exists?  
        if (update.hasMessage() && update.getMessage().hasText()) {
            String incomingText = update.getMessage().getText().toLowerCase();
            message.setChatId(String.valueOf(ChatId));
        // Processing command /start    
        String lowerCaseText = incomingText.toLowerCase(); //for the code optimization now we're setting .toLowerCase before the statement below
        // Now instead incomingText.contains("привiт") we're using modified checking
        boolean isGreeting = greetings.stream().anyMatch(greet -> lowerCaseText.contains(greet));
        if ("/start".equals(lowerCaseText)) {
            message.setText("Привiт, <i>майбутнiй</i> програмiст!");        
        } else if (isGreeting) {        
            message.setText("Як тебе звуть?"); 
            awaitingName.add(ChatId); // We note that we're waiting for the username       
        } else if (awaitingName.contains(ChatId)) {
            clearGlories(ChatId);
            addGlories(ChatId, 0);
            // The user enters his name
            chatIdToUserName.put(ChatId, incomingText); // Saving the username
            //message.setText("Радий знайомству, " + incomingText + "!");            
            awaitingName.remove(ChatId); // Removing the wait mark
            SendMessage message2 = createMessage(ChatId, STEP_1_TEXT, Map.of("Злам холодильника +20 слави","step_1_btn"));
            sendApiMethodAsync(message2);
        } 
        sendApiMethodAsync(message);
    }

    if (update.hasCallbackQuery()) {
        ChatId = update.getCallbackQuery().getMessage().getChatId(); //Checking
        if (update.getCallbackQuery().getData().equals("step_1_btn") && getGlories(ChatId) == 0) {
            addGlories(ChatId, 20);
            SendMessage message2 = createMessage(ChatId, STEP_2_TEXT, Map.of(
                "Взяти сосиску! +20 слави","step_2_btn",
                "Взяти рибку! +20 слави","step_2_btn",
                "Скинути банку з огірками! +20 слави","step_2_btn"));
            sendApiMethodAsync(message2);
        }
        if (update.getCallbackQuery().getData().equals("step_2_btn") && getGlories(ChatId) == 20) {
            addGlories(ChatId, 20);
            SendMessage message2 = createMessage(ChatId, STEP_3_TEXT, Map.of(
                "Злам робота пилососа +30 слави","step_3_btn"));
            sendApiMethodAsync(message2);
        }
        if (update.getCallbackQuery().getData().equals("step_3_btn") && getGlories(ChatId) == 40) {
            addGlories(ChatId, 30);
            SendMessage message2 = createMessage(ChatId, STEP_4_TEXT, Map.of(
                "Відправити робопилосос за їжею! +30 слави","step_4_btn",
                "Проїхатися на робопилососі! +30 слави", "step_4_btn",
                "Тікати від робопилососа! +30 слави","step_4_btn"));
            sendApiMethodAsync(message2);
        }
        if (update.getCallbackQuery().getData().equals("step_4_btn") && getGlories(ChatId) == 70) {
            addGlories(ChatId, 30);
            SendMessage message2 = createMessage(ChatId, STEP_5_TEXT, Map.of(
                "Одягнути та включити GoPro! +40 слави","step_5_btn"));
            sendApiMethodAsync(message2);
        }
        if (update.getCallbackQuery().getData().equals("step_5_btn") && getGlories(ChatId) == 100) {
            addGlories(ChatId, 40);
            SendMessage message2 = createMessage(ChatId, STEP_6_TEXT, Map.of(
                "Бігати дахами, знімати на GoPro! +40 слави","step_6_btn",
                "З GoPro нападати на інших котів із засідки! +40 слави", "step_6_btn",
                "З GoPro нападати на собак із засідки! +40 слави","step_6_btn"));
            sendApiMethodAsync(message2);
        }
        if (update.getCallbackQuery().getData().equals("step_6_btn") && getGlories(ChatId) == 140) {
            addGlories(ChatId, 40);
            SendMessage message2 = createMessage(ChatId, STEP_7_TEXT, Map.of(
                "Злам пароля +40 слави","step_7_btn"));
            sendApiMethodAsync(message2);
        }
        if (update.getCallbackQuery().getData().equals("step_7_btn") && getGlories(ChatId) == 180) {
            addGlories(ChatId, 50);
            SendMessage message2 = createMessage(ChatId, STEP_8_TEXT, Map.of(
                "Залити вiдео на комп'ютер +50 слави","step_8_btn"));                
            sendApiMethodAsync(message2);
        }
        if (update.getCallbackQuery().getData().equals("step_8_btn") && getGlories(ChatId) == 230) {
            addGlories(ChatId, 50);
            SendMessage message2 = createMessage(ChatId, FINAL_TEXT, Map.of(
                "Вийти на подвір'я","final_btn"));                
            sendApiMethodAsync(message2);
        }
        clearGlories(ChatId);
    }        
}

    private Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        return null; // или какая-то логика обработки неопределенного состояния
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new MyFirstTelegramBot());        
    }
}