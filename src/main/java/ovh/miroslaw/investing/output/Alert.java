package ovh.miroslaw.investing.output;

import com.ongres.process.FluentProcess;
import ovh.miroslaw.investing.model.Asset;
import ovh.miroslaw.investing.model.Portfolio;
import ovh.miroslaw.investing.portfolio.AlertChecker;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.CommandSpec;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static ovh.miroslaw.investing.Constant.ARG_EXCHANGE_CURRENCY;
import static ovh.miroslaw.investing.Constant.ARG_SOUND;
import static ovh.miroslaw.investing.Constant.DEFAULT_EXCHANGE_CURRENCY;
import static ovh.miroslaw.investing.Constant.getCommandParameter;

public class Alert implements OutputDecorator {

    private final List<Portfolio> portfolio;
    private final Optional<File> soundOption;
    private final Output output;
    private final AlertChecker alertChecker;

    public Alert(List<Portfolio> portfolio, CommandSpec commandSpec, Output output) {
        this.portfolio = portfolio;
        this.soundOption =  getCommandParameter(commandSpec, ARG_SOUND.toString()).flatMap(ArgSpec::getValue);
        String exchangeCurrency = getCommandParameter(commandSpec, ARG_EXCHANGE_CURRENCY.toString())
                .map(e -> (String) e.getValue())
                .orElse(DEFAULT_EXCHANGE_CURRENCY.value);
        this.alertChecker = new AlertChecker(exchangeCurrency);
        this.output = output;
    }

    @Override
    public String display(List<? extends Asset> assets) {
        String msg = output.display(assets);
        final String alertMsg = alertChecker.checkAlerts(portfolio, assets);
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
