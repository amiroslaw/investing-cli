package ovh.miroslaw.investing;

import com.ongres.process.FluentProcess;
import com.ongres.process.Output;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.jansi.graalvm.AnsiConsole;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Command(name = "investing cli", mixinStandardHelpOptions = true,
         version = "1.0",
         description = "Your investing portfolio")
class Investing implements Callable<Integer> {
    private static final String PORTFOLIO_FILE_NAME = "portfolio";
    private static final String CONFIG_FILE_NAME = "investing";

    @Option(names = {"-p", "--portfolio"}, description = "The file with the assets. Provide file or put it in one of the folders: - $HOME \n- " + CONFIG_FILE_NAME + "/" + PORTFOLIO_FILE_NAME )
    private Optional<File> portfolioFile;

    @Option(names = {"-k", "--key"}, description = "Private access key.\nGet it from https://marketstack.com/dashboard")
    private Optional<String> accessKey = Optional.of("0a2baec9d1164d8accad1ec57c55fd88");

    public static void main(String... args) {
        int exitCode;
        try (AnsiConsole ansi = AnsiConsole.windowsInstall()) {
            exitCode = new CommandLine(new Investing()).execute(args);
        }
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        final String configPath = portfolioFile.map(File::getAbsolutePath).orElse(getConfigPath());
        showNotification("test");
        try (Stream<String> stream = Files.lines(Paths.get(configPath))) {
            stream.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        fluentProcess();
        return 0;
    }

    private void showNotification(String msg) {
        FluentProcess.start("notify-send", msg);
    }

    private String getConfigPath() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        final Map<String, String> environment = processBuilder.environment();
        final Optional<String> xdgConfigHome = Optional.of(environment.get("XDG_CONFIG_HOME"));
        final String home = environment.get("HOME");
        return xdgConfigHome.map(e -> e + "/" +CONFIG_FILE_NAME )
                .orElse(home + ".") + "/" + PORTFOLIO_FILE_NAME;
    }

    private void fluentProcess() {
        final String ls = FluentProcess.builder("ls").arg("-a")
                .start()
                .stream().collect(Collectors.joining("\n"));

        final FluentProcess fluentProcess = FluentProcess.builder("ls").arg("-a").start();
        final Output output = fluentProcess.tryGet();
        output.error().ifPresent(System.out::println);
        output.output().ifPresent(System.out::println);
        fluentProcess.close();

        FluentProcess.builder("ls").arg("--fjdisf").start().tryGet().output().ifPresent(System.out::println);

        final FluentProcess process = FluentProcess.builder("ls").arg("--fjdisf").start();
        if (process.isSuccessful()) {
            process.writeToOutputStream(System.out);
        } else {
            System.out.println("error");
        }
    }

}
