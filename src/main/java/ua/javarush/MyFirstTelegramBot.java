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
    //Optimizing of .env file loading
    private static final Dotenv dotenv = Dotenv.load();
       
    @Override
    public String getBotUsername() {
        // додай ім'я бота в лапки нижче     
        return dotenv.get("BOT_USRNAME"); //For increased safety we're not using the direct bot username anymore        
    }

    @Override
    public String getBotToken() {
        // додай токен бота в лапки нижче
        return dotenv.get("BOT_TOKEN");  //For increased safety we're not using the direct bot token anymore
    }

    @Override
    public void onUpdateReceived(Update update) {
        // основний функціонал бота будемо писати тут
        Long ChatId = getChatId(update);
        //Checking, is the text message exists?  
        if (update.hasMessage() && update.getMessage().hasText()) {
            String incomingText = update.getMessage().getText().toLowerCase();
        // Processing command /start    
        String lowerCaseText = incomingText; //for the code optimization now we're setting .toLowerCase before the statement below
        // Now instead incomingText.contains("привiт") we're using modified checking
        boolean isGreeting = greetings.stream().anyMatch(greet -> lowerCaseText.contains(greet));
        if ("/start".equalsIgnoreCase (lowerCaseText)) {
            SendMessage message = createMessage(ChatId,"Привiт, <i>майбутнiй</i> програмiст!");
            sendApiMethodAsync(message);
            clearGlories(ChatId); //Cleaning glories after bot restarting       
        } else if (isGreeting) {        
            SendMessage message = createMessage(ChatId,"Як тебе звуть?");
            sendApiMethodAsync(message);
            awaitingName.add(ChatId); // We note that we're waiting for the username       
        } else if (awaitingName.contains(ChatId)) {            
            addGlories(ChatId, 0);
            // The user enters his name
            chatIdToUserName.put(ChatId, incomingText); // Saving the username                       
            awaitingName.remove(ChatId); // Removing the wait mark
            // Creating and sending photomessage (more details in sendPhotoAsync method below)
            SendMessage(ChatId, 0, "step_1_pic", STEP_1_TEXT, Map.of("Злам холодильника +20 слави","step_1_btn"));          
        } 
        
    }

    if (update.hasCallbackQuery()) {
        ChatId = update.getCallbackQuery().getMessage().getChatId(); //Checking
        if (update.getCallbackQuery().getData().equals("step_1_btn") && getGlories(ChatId) == 0) {
            SendMessage(ChatId, 20, "step_2_pic", STEP_2_TEXT, Map.of(
                "Взяти сосиску! +20 слави","step_2_btn",
                "Взяти рибку! +20 слави","step_2_btn",
                "Скинути банку з огірками! +20 слави","step_2_btn"));                     
        }
        if (update.getCallbackQuery().getData().equals("step_2_btn") && getGlories(ChatId) == 20) {
            SendMessage(ChatId, 20, "step_3_pic", STEP_3_TEXT, Map.of(
                "Злам робота пилососа +30 слави","step_3_btn"));         
        }
        if (update.getCallbackQuery().getData().equals("step_3_btn") && getGlories(ChatId) == 40) {
            SendMessage(ChatId, 30, "step_4_pic", STEP_4_TEXT, Map.of(
                "Відправити робопилосос за їжею! +30 слави","step_4_btn",
                "Проїхатися на робопилососі! +30 слави", "step_4_btn",
                "Тікати від робопилососа! +30 слави","step_4_btn"));         
        }
        if (update.getCallbackQuery().getData().equals("step_4_btn") && getGlories(ChatId) == 70) {
            SendMessage(ChatId, 30, "step_5_pic", STEP_5_TEXT, Map.of(
                "Одягнути та включити GoPro! +40 слави","step_5_btn"));          
        }
        if (update.getCallbackQuery().getData().equals("step_5_btn") && getGlories(ChatId) == 100) {
            SendMessage(ChatId, 40, "step_6_pic", STEP_6_TEXT, Map.of(
                "Бігати дахами, знімати на GoPro! +40 слави","step_6_btn",
                "З GoPro нападати на інших котів із засідки! +40 слави", "step_6_btn",
                "З GoPro нападати на собак із засідки! +40 слави","step_6_btn"));          
        }
        if (update.getCallbackQuery().getData().equals("step_6_btn") && getGlories(ChatId) == 140) {
            SendMessage(ChatId, 40, "step_7_pic", STEP_7_TEXT, Map.of(
                "Злам пароля +40 слави","step_7_btn"));          
        }
        if (update.getCallbackQuery().getData().equals("step_7_btn") && getGlories(ChatId) == 180) {
            SendMessage(ChatId, 50, "step_8_pic", STEP_8_TEXT, Map.of(
                "Залити вiдео на комп'ютер +50 слави","step_8_btn"));           
        }
        if (update.getCallbackQuery().getData().equals("step_8_btn") && getGlories(ChatId) == 230) {
            SendMessage(ChatId, 50, "final_pic", FINAL_TEXT, Map.of(
                "Вийти на подвір'я","final_btn"));                         
            clearGlories(ChatId); //Cleaning glories
        }
        
    }        
}

private void SendMessage(Long ChatId, int glories, String picName, String text, Map<String, String> buttons) {
    try {
    addGlories(ChatId, glories);
    SendPhoto photo = createPhotoMessage(ChatId, picName);
    executeAsync(photo); //Sending photomessage async
    SendMessage message = createMessage(ChatId, text, buttons);
    sendApiMethodAsync(message); //Sending message async
    } catch (RuntimeException e) {
        // Handling of exceptions, possibly logging or sending an error message to the user
        System.out.println("Sending error: " + e.getMessage());
    }
}

    private Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        return null; // or some logic for handling an undefined state
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new MyFirstTelegramBot());        
    }
}