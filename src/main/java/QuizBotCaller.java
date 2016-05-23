import com.drawers.dao.MqttChatMessage;
import com.drawers.dao.packets.MqttProviderManager;
import com.drawers.dao.packets.SubscribeOthers;
import org.drawers.bot.listener.DrawersMessageListener;
import org.drawers.bot.mqtt.DrawersBot;
import org.drawers.bot.util.SendMail;

import java.util.concurrent.TimeUnit;

/**
 * Created by harshit on 23/5/16.
 */
public class QuizBotCaller implements DrawersMessageListener {
    private static DrawersBot bot;
    private static QuizBotCaller client;
    private MqttProviderManager mqttProviderManager;
    private String clientId;

    public QuizBotCaller(String clientId, String password) {
        bot = new DrawersBot(clientId, password, this);
        mqttProviderManager = MqttProviderManager.getInstanceFor(bot);
    }

    public static void main(String[] args) {
        if(args.length != 3) {
            System.out.println("Usage: java DrawersClientCli <clientId> <password> <admin-email-id>");
        } else {
            String clientId = args[0];
            String password = args[1];
            String adminEmail = args[2];
            SendMail.getInstance().setAdminEmail(adminEmail);
            TriviaRetrofitAdapter.getTriviaRetrofitAdapter();
            SendMail.getInstance().sendMail("Welcome to Drawers Bot", "Your bot is up and running now.");
            client = new QuizBotCaller(clientId, password);
            client.clientId = clientId;
            client.startBot();
        }
    }

    private void startBot() {
        mqttProviderManager.addMessageListener(new QuizMessageListener(bot, clientId));
        mqttProviderManager.addChatStateListener(new QuizChatStateListener(bot, clientId));
        bot.start();
        try {
            bot.getExecutorService().awaitTermination(100000000L, TimeUnit.SECONDS);
        } catch (InterruptedException var2) {
            var2.printStackTrace();
        }

    }

    public void onConnected() {
        bot.subscribe(new SubscribeOthers(SubscribeOthers.OTHERS_NAMESPACE, clientId).getChannel(), 1, null, null);
    }

    public MqttChatMessage processMessageAndReply(MqttChatMessage message) {
        System.out.println("Received new message: " + message);
        return message;
    }
}
