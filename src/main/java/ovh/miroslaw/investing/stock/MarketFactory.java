package ovh.miroslaw.investing.stock;

import ovh.miroslaw.investing.RetrievingDataException;
import ovh.miroslaw.investing.model.Asset;
import ovh.miroslaw.investing.model.Portfolio;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Model.OptionSpec;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class MarketFactory {

    public Optional<String> getAccessKey() {
        return accessKey;
    }

    private final CommandSpec commandSpec;

    private final Optional<String> exchangeCurrency;

    public Optional<String> getExchangeCurrency() {
        return exchangeCurrency;
    }

    Optional<String> accessKey;
    private static boolean errorOption;
    final BiFunction<Optional<String>, String, YahooService> getYahooService = (currency, key) -> currency
            .map(e -> new YahooService(key, e))
            .orElse(new YahooService(key));

    public MarketFactory(CommandSpec commandSpec) {
        this.commandSpec = commandSpec;
        exchangeCurrency = getCommandParameter("-e").flatMap(ArgSpec::getValue);
        accessKey = getCommandParameter("-k").flatMap(ArgSpec::getValue);
        errorOption = getCommandParameter("--no-errors")
                .map(e -> (Boolean) e.getValue())
                .orElse(true);
    }

    public List<Asset> getAssets(List<Portfolio> portfolio, MarketEnum market) {
        return (List<Asset>) market.assetsInfo.apply(this, portfolio);
    }

    public List<Asset> getAssets(List<Portfolio> portfolio, MarketEnum stock, MarketEnum crypto) {
        return combineAssets(stock.assetsInfo.apply(this, portfolio), crypto.assetsInfo.apply(this, portfolio));
    }

    private List<Asset> combineAssets(List<? extends Asset> stockAssets, List<? extends Asset> cryptoAssets) {
        return Stream.concat(stockAssets.stream(), cryptoAssets.stream()).toList();
    }

    private Optional<OptionSpec> getCommandParameter(String paramName) {
        return commandSpec.options().stream()
                .filter(e -> e.shortestName().equals(paramName))
                .findAny();
    }

    List<? extends Asset> getMarketstackAssets(List<Portfolio> e) {
        Optional<MarketstackService> marketstack = accessKey.map(MarketstackService::new);
        if (marketstack.isPresent()) {
            try {
                return marketstack.get().getAssetsInfo(e);
            } catch (RetrievingDataException exception) {
                printError("MarketStack.com");
            }
        }
        return Collections.emptyList();
    }

    public static void printError(String assetName) {
        if (errorOption) {
            System.out.println(
                    Ansi.AUTO.string("@|bold,red Couldn't fetch information about + " + assetName + "!|@"));
        }
    }
}
