import com.drawers.dao.ChatConstant;
import com.drawers.dao.MqttChatMessage;
import com.drawers.dao.packets.MqttChat;
import com.drawers.dao.packets.listeners.NewMessageListener;
import org.drawers.bot.mqtt.DrawersBot;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by harshit on 23/5/16.
 */
public class QuizMessageListener implements NewMessageListener {
    private final DrawersBot bot;
    private final String clientId;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    public QuizMessageListener(DrawersBot bot, String clientId) {
        this.bot = bot;
        this.clientId = clientId;
    }

    @Override
    public void receiveMessage(MqttChatMessage mqttChatMessage) {

        String message = null;
        try {
            message = URLDecoder.decode(mqttChatMessage.message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (message == null) {
            return;
        }
        if (TriviaRetrofitAdapter.currentQuestionsMap.containsKey(mqttChatMessage.senderUid)
                && TriviaRetrofitAdapter.currentQuestionsMap.get(mqttChatMessage.senderUid).enqued) {
            final String finalMessage = message;
            executorService.submit((Runnable) () -> {
                generateAnswer(mqttChatMessage, finalMessage);
                generateQuestion(mqttChatMessage);
            });
        } else {
            final String finalMessage1 = message;
            executorService.submit((Runnable) () -> {
                generateQuestion(mqttChatMessage);
            });
        }
    }

    private void generateAnswer(MqttChatMessage mqttChatMessage, String finalMessage) {
        if (finalMessage != null) {
            int answer = TriviaRetrofitAdapter.isValidOption(finalMessage);
            if (answer > 0 && answer < 5) {
                String output = TriviaRetrofitAdapter.getTriviaRetrofitAdapter().
                        validateAnswer(mqttChatMessage.senderUid, answer);
                new MqttChat(mqttChatMessage.senderUid,
                        UUID.randomUUID().toString(), output, false,
                        ChatConstant.ChatType.TEXT, clientId).sendStanza(bot);
            }
        }
    }

    private void generateQuestion(MqttChatMessage mqttChatMessage) {
        Question question = TriviaRetrofitAdapter.getTriviaRetrofitAdapter().getQuestion(mqttChatMessage.senderUid);
        String output = TriviaRetrofitAdapter.generateString(question);
        new MqttChat(mqttChatMessage.senderUid,
                UUID.randomUUID().toString(), output, false,
                ChatConstant.ChatType.TEXT, clientId).sendStanza(bot);
    }

    @Override
    public void acknowledgeStanza(MqttChatMessage mqttChatMessage) {

    }
}
