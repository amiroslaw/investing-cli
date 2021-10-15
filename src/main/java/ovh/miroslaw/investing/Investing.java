package ovh.miroslaw.investing;

import ovh.miroslaw.investing.model.Asset;
import ovh.miroslaw.investing.model.Portfolio;
import ovh.miroslaw.investing.output.Alert;
import ovh.miroslaw.investing.output.Console;
import ovh.miroslaw.investing.output.Notification;
import ovh.miroslaw.investing.output.Output;
import ovh.miroslaw.investing.output.Profit;
import ovh.miroslaw.investing.output.ShortWithSymbol;
import ovh.miroslaw.investing.output.Verbose;
import ovh.miroslaw.investing.portfolio.PortfolioFilter;
import ovh.miroslaw.investing.portfolio.PortfolioReader;
import ovh.miroslaw.investing.stock.MarketEnum;
import ovh.miroslaw.investing.stock.MarketFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.jansi.graalvm.AnsiConsole;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import static ovh.miroslaw.investing.portfolio.PortfolioReader.CONFIG_FILE_NAME;
import static ovh.miroslaw.investing.portfolio.PortfolioReader.PORTFOLIO_FILE_NAME;
import static ovh.miroslaw.investing.portfolio.PortfolioReader.getConfigPath;

@Command(name = "investing cli", mixinStandardHelpOptions = true,
         version = "1.0",
         description = "Your investing portfolio")
class Investing implements Callable<Integer> {

    @Spec
    CommandSpec commandSpec;

    @Option(names = "--no-errors", negatable = true,
            description = "Doesn't print errors in the output")
    static boolean errorsOption = true;

    @Option(names = {"-v", "--verbose"},
            description = "Shows verbose output.")
    private boolean verboseOption;

    @Option(names = {"-l", "--one-line"},
            description = "Prints short output in one line. Good for showing information on a status bar.")
    private boolean barOutputOption;

    @Option(names = {"-p", "--portfolio"},
            description = """
                    Shows profits from your portfolio. If sell or buy price are not provided in the config file it will calculate revenue.
                    Remember to change --exchange-currency in order to have the same currency in all markets. 
                    """)
    private boolean holdingOption;

    @Option(names = {"-n", "--notify"},
            description = "Shows notification in dunst.")
    private boolean notifyOption;

    @Option(names = {"-a", "--alert"},
            description = "Check out alerts. With the --sound option it will play sound")
    private boolean alertOption;

    @Option(names = {"-c", "--configuration"},
            description =
                    "The file with the assets that you want to trace or you own. Provide file or put it in one of the folders: - $HOME"
                            + "/" + PORTFOLIO_FILE_NAME +
                            "\n- " + CONFIG_FILE_NAME + "/" + PORTFOLIO_FILE_NAME)
    private Optional<File> portfolioFile;

    @Option(names = {"-e", "--exchange-currency"}, description = "Exchange currency for cryptocurrency currency pair.")
    private Optional<String> exchangeCurrency;

    @Option(names = {"-o", "--only"},
            description = "Process only for specific assets. Provide list of the assets symbol separated by comma")
    private Optional<String> onlyAssetsOption;

    @Option(names = {"-t", "--type"}, description = "Process only for specific assets. Accepts: CRYPTO, GPW, STOCK, CC (crypto crypto)")
    private Optional<String> typeOption;

    @Option(names = {"-k", "--key"}, description = "Private access key.\nGet it from https://marketstack.com/dashboard")
    private Optional<String> accessKey;

    @Option(names = {"-s", "--sound"},
            description = "Audio file for the sound alerts. It requires ffmpeg.")
    private Optional<File> soundAlert;

    public static void main(String... args) {
        int exitCode;
        try (AnsiConsole ansi = AnsiConsole.windowsInstall()) {
            exitCode = new CommandLine(new Investing()).execute(args);
        }
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        commandSpec.commandLine().setExecutionExceptionHandler(new PrintExceptionMessageHandler(errorsOption));
        final File portfolioConfig = portfolioFile.orElse(getConfigPath().toFile());
        List<Portfolio> portfolio = PortfolioReader.getPortfolio(portfolioConfig);
        PortfolioFilter filter = new PortfolioFilter(portfolio);
        portfolio = filter.applyFilter(typeOption, onlyAssetsOption);

        MarketFactory marketFactory = new MarketFactory(commandSpec);
        final List<? extends Asset> assets = marketFactory.getAssets(portfolio, MarketEnum.YAHOO);

        Output output = new Output() {};
        if (barOutputOption) {
            output = new ShortWithSymbol(portfolio, output);
        }
        if (verboseOption) {
            output = new Verbose(output);
        }
        if (holdingOption) {
            output = new Profit(portfolio, output);
        }
        if (alertOption) {
            output = new Alert(portfolio, soundAlert, output);
        }
        if (notifyOption) {
            output = new Notification(output);
        }

        output = new Console(output);
        output.display(assets);

        return 0;
    }

}
