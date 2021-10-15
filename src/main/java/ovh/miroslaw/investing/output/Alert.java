package ovh.miroslaw.investing.output;

import com.ongres.process.FluentProcess;
import ovh.miroslaw.investing.model.Asset;
import ovh.miroslaw.investing.model.Portfolio;
import ovh.miroslaw.investing.portfolio.AlertChecker;
import picocli.CommandLine.Help.Ansi;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class Alert implements OutputDecorator {

    private final List<Portfolio> portfolio;
    private final Optional<File> soundOption;
    private final Output output;

    public Alert(List<Portfolio> portfolio, Optional<File> soundOption, Output output) {
        this.portfolio = portfolio;
        this.soundOption = soundOption;
        this.output = output;
    }

    @Override
    public String display(List<? extends Asset> assets) {
        String msg = output.display(assets);
        final String alertMsg = AlertChecker.checkAlerts(portfolio, assets);
        soundOption.stream()
                .filter(e -> !alertMsg.isBlank())
                .forEach(this::soundNotification);

        msg = msg.isBlank() ? "" : msg + System.lineSeparator();
        return msg + alertMsg;
    }

    public void soundNotification(File sound) {
        FluentProcess.start("ffplay", "-nodisp", "-autoexit", "-loglevel", "-8", "-volume", "10", sound.getAbsolutePath())
                .tryGet().exception().ifPresent(e -> System.out.println(
                Ansi.AUTO.string("@|bold,red Couldn't play sound alert " + sound.getAbsolutePath() + ".|@"))
        );
    }
}
