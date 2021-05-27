package ovh.miroslaw.investing.output;

import com.ongres.process.FluentProcess;
import picocli.CommandLine.Help.Ansi;

public class NotificationSender {

    private NotificationSender() {
    }

    public static void notify(String msg) {
        if (!msg.isBlank()) {
            sendNotification(msg);
        }
    }

    private static void sendNotification(String msg) {
        FluentProcess.start("notify-send", "-u", "critical", msg)
                .tryGet().exception().ifPresent(e -> System.out.println(
                Ansi.AUTO.string("@|bold,red Couldn't send notification.|@")));
    }
}
