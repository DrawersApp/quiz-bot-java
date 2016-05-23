import com.drawers.dao.ChatConstant;
import com.drawers.dao.packets.ChatState;
import com.drawers.dao.packets.MqttChat;
import com.drawers.dao.packets.listeners.ChatStateListener;
import org.drawers.bot.mqtt.DrawersBot;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by harshit on 23/5/16.
 */
public class QuizChatStateListener implements ChatStateListener {

    private final DrawersBot bot;
    private final String clientId;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public QuizChatStateListener(DrawersBot bot, String clientId) {
        this.bot = bot;
        this.clientId = clientId;
    }

    @Override
    public void notifyActiveListener(ChatState.ChatStateValues chatStateValues, String s) {
        if (!TriviaRetrofitAdapter.currentQuestionsMap.containsKey(s)) {
            executorService.submit((Runnable) () -> new MqttChat(s,
                    UUID.randomUUID().toString(), "Type something to get started", false,
                    ChatConstant.ChatType.TEXT, clientId).sendStanza(bot));
        }
    }
}
